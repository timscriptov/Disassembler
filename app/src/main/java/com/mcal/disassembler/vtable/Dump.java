package com.mcal.disassembler.vtable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;

class Dump {
    private final byte[] bs;
    Elf elf;

    Dump(String path) {
        bs = Utils.readFile(path);
        elf = new Elf();

        Header h = elf.hdr;
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
            Section shdr = new Section();
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
        for (Section sh : elf.sections) {
            sh.name = getString(elf.sections.get(h.shstrndx), Integer.parseInt(sh.name));
        }


        for (int i = 0; i < elf.hdr.phnum; ++i) {
            byte[] ph = Utils.cp(bs, h.phoff + i * h.phentsize, h.phentsize);
            Segment phdr = new Segment();
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
            for (Section psec : elf.sections) {
                if (((psec.flags & 2) != 0) ? (phdr.vaddr <= psec.addr && psec.addr + psec.size <= endaddr) : (phdr.offset <= psec.offset && psec.offset + psec.size <= endoff)) {
                    phdr.sections.add(psec);
                }
            }
        }
    }

    Vector<Symbol> getSyms() {
        Vector<Symbol> syms = new Vector<>();
        for (Section sec : elf.sections) {
            if (sec.type == 2 || sec.type == 11) {
                for (int i = 0; i < getSymNum(sec); ++i) {
                    syms.add(getSym(sec, i));
                }
            }
        }
        return syms;
    }

    int getSymNum(@NotNull Section sec) {
        return sec.size / 16;
    }

    Symbol getSym(@NotNull Section sec, int index) {
        byte[] des = Utils.cp(bs, index * 16 + sec.offset, 16);
        Symbol sym = new Symbol();
        sym.name = getString(elf.sections.get(sec.link), Utils.cb2i(des, 0, 4));
        sym.value = Utils.cb2i(des, 4, 4);
        sym.size = Utils.cb2i(des, 8, 4);
        sym.other = des[13];
        sym.shndx = Utils.cb2i(des, 14, 2);
        sym.bind = des[12] >> 4;
        sym.type = des[12] & 0xf;

        return sym;
    }

    int getRelNum(@NotNull Section sec) {
        return sec.size / sec.entsize;
    }

    Vector<Relocation> getRels() {
        Vector<Relocation> rels = new Vector<>();
        for (Section sec : elf.sections) {
            if (sec.type == 9) {
                for (int i = 0; i < getRelNum(sec); ++i) {
                    rels.add(getRel(sec, i));
                }
            }
        }

        return rels;
    }

    Relocation getRel(@NotNull Section sec, int index) {
        byte[] des = Utils.cp(bs, index * 8 + sec.offset, 8);
        Relocation rel = new Relocation();
        rel.offset = Utils.cb2i(des, 0, 4);
        rel.info = Utils.cb2i(des, 4, 4);
        return rel;
    }

    Symbol Rel2Sym(Relocation rel) {
        for (Section sec : elf.sections) {
            if (sec.type == 11) {
                return getSym(sec, rel.info >> 8);
            }
        }
        return null;
    }

    @NotNull
    @Contract("_, _ -> new")
    private String getString(@NotNull Section strtb, int off) {
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
