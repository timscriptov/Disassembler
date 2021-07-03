#include <jni.h>
#include <fstream>
#include <string>
#include <vector>
#include <cxxabi.h>
#include <sys/stat.h>

#include "elfio/elfio_dump.hpp"
#include "elfio/elf_types.hpp"
#include "elfio/elfio_section.hpp"
#include "elfio/elfio_symbols.hpp"

using namespace ELFIO;

struct DisassemblerSymbol {
	std::string   name;
	Elf64_Addr    value;
    Elf_Xword     size;
    unsigned char bind;
    unsigned char type;
    Elf_Half      section;
    unsigned char other;
};

std::vector<DisassemblerSymbol>disassemblerSymbolsList;

void loadSymbols(const elfio& reader) {
	disassemblerSymbolsList.clear();
	Elf_Half n = reader.sections.size();
    for (Elf_Half i = 0; i < n; ++i) {
		section* sec = reader.sections[i];
		if (SHT_SYMTAB == sec->get_type() || SHT_DYNSYM == sec->get_type()) {
			symbol_section_accessor symbols(reader, sec);
			long long sym_no = symbols.get_symbols_num();
			if (sym_no > 0) {
				for (long long i = 0; i < sym_no; ++i) {
					DisassemblerSymbol symbol_disassembler;
                    symbols.get_symbol(static_cast<Elf_Xword>(i), symbol_disassembler.name, symbol_disassembler.value, symbol_disassembler.size, symbol_disassembler. bind, symbol_disassembler. type, symbol_disassembler.section, symbol_disassembler.other);
					disassemblerSymbolsList.push_back(symbol_disassembler);
                }
            }
        }
    }
}

std::string jstringTostring(JNIEnv* env, jstring jstr) {
	char* rtn = nullptr;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("GB2312");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    auto barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if(alen > 0) {
		rtn = (char*)malloc(static_cast<size_t>(alen + 1));
		memcpy(rtn, ba, static_cast<size_t>(alen));
		rtn[alen]=0;
	}
	env->ReleaseByteArrayElements(barr,ba,0);
	std::string stemp(rtn);
	free(rtn);
	return stemp;
}

extern "C" {
    JNIEXPORT jboolean JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_hasFile(JNIEnv* env, jclass type, jstring path) {
	    std::ifstream istream(jstringTostring(env,path).c_str());
	    return static_cast<jboolean>(istream.is_open());
    }
    JNIEXPORT jstring JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_getNameAt(JNIEnv *env, jclass type, jlong pos) {
	    return env->NewStringUTF(disassemblerSymbolsList[pos].name.c_str());
    }
    JNIEXPORT jstring JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_getDemangledNameAt(JNIEnv *env, jclass type, jlong pos) {
	    char*name=abi::__cxa_demangle(disassemblerSymbolsList[pos].name.c_str(),0,0,0);
	    return env->NewStringUTF(name?name:"");
    }
    JNIEXPORT jlong JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_getSize(JNIEnv* env, jclass type) {
	    return disassemblerSymbolsList.size();
    }
    JNIEXPORT jint JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_getTypeAt(JNIEnv *env, jclass type, jlong pos) {
	    return (jint)((int)disassemblerSymbolsList[pos].type);
    }
    JNIEXPORT jint JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_getBindAt(JNIEnv *env, jclass type, jlong pos) {
	    return (jint)((int)disassemblerSymbolsList[pos].bind);
    }
    JNIEXPORT void JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_load(JNIEnv* env, jclass type, jstring path) {
	    elfio reader;
	    reader.load(jstringTostring(env,path));
	    loadSymbols(reader);
    }
    JNIEXPORT jstring JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_demangleOnly(JNIEnv* env, jclass type, jstring jname) {
	    char*name=abi::__cxa_demangle(jstringTostring(env,jname).c_str(),0,0,0);
	    return env->NewStringUTF(name?name:"");
    }
    JNIEXPORT jstring JNICALL Java_com_mcal_disassembler_nativeapi_DisassemblerDumper_demangle(JNIEnv* env, jclass type, jstring name) {
		std::string methodsName=jstringTostring(env,name);

		std::string bridgeString;
		std::vector<std::string>strings;
		std::string result;

		for(char letter:methodsName) {
			if(letter == '\n' && !bridgeString.empty()) {
				strings.push_back(bridgeString);
				bridgeString = "";
			} else bridgeString += letter;
		}
		if(!bridgeString.empty())
			strings.push_back(bridgeString);

		for(const std::string& string:strings) {
			if(abi::__cxa_demangle(string.c_str(),nullptr,nullptr,nullptr)) {
				result += abi::__cxa_demangle(string.c_str(),nullptr,nullptr,nullptr);
				result += "\n";
			} else if(!string.empty()) {
				result += string;
				result += "\n";
			}
		}
		return env->NewStringUTF(result.c_str());
    }
}