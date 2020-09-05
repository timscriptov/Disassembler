package com.mcal.disassembler.vtable;

import java.util.Vector;

class Dump {
    Elf elf;
    private byte[] bs;

    Dump(String path) {
        bs = Utils.readFile(path);
        elf = new Elf();

        header h = elf.hdr;
        h.ident = Utils.cp(bs, 0, 16);
        h.type = Utils.cb2i(bs, 16, 2);
        h.machine = Utils.cb2i(bs, 18, 2);
        h.version = Utils.cb2i(bs, 20, 4);
        h.entry = Utils.cb2i(bs, 24, 4);
        h.phoff = Utils.cb2i(bs, 28, 4);
        h.shoff = Utils.cb2i(bs, 32, 4);
        h.flags = Utils.cb2i(bs, 36, 4);
        h.ehsize = Utils.cb2i(bs, 40, 2);
        h.phentsize = Utils.cb2i(bs, 42, 2);
        h.phnum = Utils.cb2i(bs, 44, 2);
        h.shentsize = Utils.cb2i(bs, 46, 2);
        h.shnum = Utils.cb2i(bs, 48, 2);
        h.shstrndx = Utils.cb2i(bs, 50, 2);

        for (int i = 0; i < h.shnum; ++i) {
            byte[] sh = Utils.cp(bs, h.shoff + i * h.shentsize, h.shentsize);
            section shdr = new section();
            shdr.name = "" + Utils.cb2i(sh, 0, 4);
            shdr.type = Utils.cb2i(sh, 4, 4);
            shdr.flags = Utils.cb2i(sh, 8, 4);
            shdr.addr = Utils.cb2i(sh, 12, 4);
            shdr.offset = Utils.cb2i(sh, 16, 4);
            shdr.size = Utils.cb2i(sh, 20, 4);
            shdr.link = Utils.cb2i(sh, 24, 4);
            shdr.info = Utils.cb2i(sh, 28, 4);
            shdr.addralign = Utils.cb2i(sh, 32, 4);
            shdr.entsize = Utils.cb2i(sh, 36, 4);
            elf.sections.add(shdr);
        }
        for (section sh : elf.sections) {
            sh.name = getString(elf.sections.get(h.shstrndx), Integer.valueOf(sh.name));
        }


        for (int i = 0; i < elf.hdr.phnum; ++i) {
            byte[] ph = Utils.cp(bs, h.phoff + i * h.phentsize, h.phentsize);
            segment phdr = new segment();
            phdr.type = Utils.cb2i(ph, 0, 4);
            phdr.offset = Utils.cb2i(ph, 4, 4);
            phdr.vaddr = Utils.cb2i(ph, 8, 4);
            phdr.paddr = Utils.cb2i(ph, 12, 4);
            phdr.filesz = Utils.cb2i(ph, 16, 4);
            phdr.memsz = Utils.cb2i(ph, 20, 4);
            phdr.flags = Utils.cb2i(ph, 24, 4);
            phdr.align = Utils.cb2i(ph, 28, 4);
            elf.segments.add(phdr);

            int endoff = phdr.offset + phdr.filesz;
            int endaddr = phdr.vaddr + phdr.memsz;
            for (section psec : elf.sections) {
                if (((psec.flags & 2) != 0) ? (phdr.vaddr <= psec.addr && psec.addr + psec.size <= endaddr) : (phdr.offset <= psec.offset && psec.offset + psec.size <= endoff)) {
                    phdr.sections.add(psec);
                }
            }
        }
    }

    Vector<symbol> getSyms() {
        Vector<symbol> syms = new Vector<>();
        for (section sec : elf.sections) {
            if (sec.type == 2 || sec.type == 11) {
                for (int i = 0; i < getSymNum(sec); ++i) {
                    syms.add(getSym(sec, i));
                }
            }
        }
        return syms;
    }

    int getSymNum(section sec) {
        return sec.size / 16;
    }

    symbol getSym(section sec, int index) {
        byte[] des = Utils.cp(bs, index * 16 + sec.offset, 16);
        symbol sym = new symbol();
        sym.name = getString(elf.sections.get(sec.link), Utils.cb2i(des, 0, 4));
        sym.value = Utils.cb2i(des, 4, 4);
        sym.size = Utils.cb2i(des, 8, 4);
        sym.other = des[13];
        sym.shndx = Utils.cb2i(des, 14, 2);
        sym.bind = des[12] >> 4;
        sym.type = des[12] & 0xf;

        return sym;
    }

    int getRelNum(section sec) {
        return sec.size / sec.entsize;
    }

    Vector<relocation> getRels() {
        Vector<relocation> rels = new Vector<>();
        for (section sec : elf.sections) {
            if (sec.type == 9) {
                for (int i = 0; i < getRelNum(sec); ++i) {
                    rels.add(getRel(sec, i));
                }
            }
        }

        return rels;
    }

    relocation getRel(section sec, int index) {
        byte[] des = Utils.cp(bs, index * 8 + sec.offset, 8);
        relocation rel = new relocation();
        rel.offset = Utils.cb2i(des, 0, 4);
        rel.info = Utils.cb2i(des, 4, 4);
        return rel;
    }

    symbol Rel2Sym(relocation rel) {
        for (section sec : elf.sections) {
            if (sec.type == 11) {
                return getSym(sec, rel.info >> 8);
            }
        }
        return null;
    }

    private String getString(section strtb, int off) {
        Vector<Byte> tmp = new Vector<>();
        for (int i = 0; ; ++i) {
            byte b = bs[strtb.offset + off + i];
            if (b == 0) {
                break;
            }
            tmp.add(b);
        }
        byte[] r = new byte[tmp.size()];
        for (int i = 0; i < tmp.size(); ++i) {
            r[i] = tmp.get(i);
        }
        return new String(r);
    }
}