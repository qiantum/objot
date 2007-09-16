package objot.bytecode;

import objot.util.Array2;
import objot.util.Math2;

import java.io.PrintStream;


public class Code
	extends Element
{
	public final Constants cons;
	protected int attrNameCi;
	protected int stackN;
	protected int localN;
	protected int addrN0;
	protected int addrN;
	protected int addrBi;
	protected byte[] ins;
	protected int catchBi;
	protected CodeCatchs catchs;
	protected int catchBn;
	protected int attrN;
	protected int attrBi;
	protected int linesBi;
	protected CodeLines lines;
	protected int varsBi;
	protected CodeVars vars;
	protected int varSignsBi;
	protected CodeVars varSigns;

	public Code(Constants c, byte[] bs, int beginBi_, boolean forExtension_)
	{
		super(bs, beginBi_, forExtension_);
		cons = c;
		attrNameCi = read0u2(beginBi);
		stackN = read0u2(beginBi + 6);
		localN = read0u2(beginBi + 8);
		addrN0 = addrN = read0u4(beginBi + 10);
		if (addrN > 65535)
			throw new ClassFormatError("too large code");
		addrBi = beginBi + 14;
		ins = bytes;
		catchBi = addrBi + addrN;
		catchBn = CodeCatchs.readByteN(bytes, catchBi);
		attrBi = catchBi + catchBn + 2;
		attrN = read0u2(attrBi - 2);
		int bi = attrBi;
		for (int an = attrN; an > 0; an--)
		{
			int name = read0u2(bi);
			if (linesBi <= 0 && cons.equalsUtf(name, Bytecode.CODE_LINES))
				linesBi = bi;
			else if (varsBi <= 0 && cons.equalsUtf(name, Bytecode.CODE_VARS))
				varsBi = bi;
			else if (varSignsBi <= 0 && cons.equalsUtf(name, Bytecode.CODE_VARSIGNS))
				varSignsBi = bi;
			bi += 6 + read0u4(bi + 2);
		}
		if (bi - beginBi - 6 != read0u4(beginBi + 2))
			throw new ClassFormatError("inconsistent attribute length");
		end1Bi = bi;
	}

	public int getStackN()
	{
		return stackN;
	}

	public int getLocalN()
	{
		return localN;
	}

	public int getAddrN()
	{
		return addrN;
	}

	public int getAddrBi()
	{
		return addrBi;
	}

	public byte readInsS1(int addr)
	{
		return ins[addr + addrBi];
	}

	public int readInsU1(int addr)
	{
		return ins[addr + addrBi] & 0xFF;
	}

	public short readInsS2(int addr)
	{
		return readS2(ins, addr + addrBi);
	}

	public int readInsU2(int addr)
	{
		return readU2(ins, addr + addrBi);
	}

	public int readInsS4(int addr)
	{
		return readS4(ins, addr + addrBi);
	}

	public int readInsU4(int addr)
	{
		return readU4(ins, addr + addrBi);
	}

	public long readInsS8(int addr)
	{
		return readS8(ins, addr + addrBi);
	}

	public int readInsAddrN(int addr)
	{
		return Opcode.readInsAddrN(ins, addr + addrBi, addrBi);
	}

	public void copyInsTo(int addr, byte[] dest, int destBi, int n)
	{
		System.arraycopy(ins, addr + addrBi, dest, destBi, n);
	}

	protected byte[] insBytes()
	{
		return ins;
	}

	public CodeCatchs getCatchs()
	{
		if (catchs == null)
			catchs = new CodeCatchs(bytes, catchBi);
		return catchs;
	}

	public int getAttrN()
	{
		return attrN;
	}

	public int getAttrBi()
	{
		return attrBi;
	}

	public CodeLines getLines()
	{
		if (lines == null && linesBi > 0)
			lines = new CodeLines(bytes, linesBi, forExtension);
		return lines;
	}

	public CodeVars getVars()
	{
		if (vars == null && varsBi > 0)
			vars = new CodeVars(bytes, varsBi, false, forExtension);
		return vars;
	}

	public CodeVars getVarSigns()
	{
		if (varSigns == null && varSignsBi > 0)
			varSigns = new CodeVars(bytes, varSignsBi, true, forExtension);
		return varSigns;
	}

	@Override
	protected void printContents(PrintStream out, int indent1st, int indent, int verbose,
		boolean hash)
	{
		out.println();
		cons.printIdentityLn(out, indent, hash);
		printIndent(out, indent);
		out.print("stackN ");
		out.print(getStackN());
		out.print(" localN ");
		out.println(getLocalN());
		printIndent(out, indent);
		out.print("addrN ");
		out.print(getAddrN());
		if (hash)
		{
			out.print(' ');
			out.print(insBytes());
		}
		out.println();
		if (verbose >= 2)
			for (int i = 0; i < getAddrN(); i += readInsAddrN(i))
			{
				printIndent(out, indent);
				out.print(i);
				out.print(". ");
				Opcode.println(this, i, out, 0, indent + 2, verbose);
			}
		getCatchs().printTo(out, indent, indent, verbose, hash);
		printIndent(out, indent);
		out.print("attrN ");
		out.println(getAttrN());
		if (getLines() != null)
			getLines().printTo(out, indent, indent, verbose, hash);
		if (getVars() != null)
			getVars().printTo(out, indent, indent, verbose, hash);
		if (getVarSigns() != null)
		{
			printIndent(out, indent);
			out.print("varSigns ");
			getVarSigns().printTo(out, 0, indent, verbose, hash);
		}
	}

	public void setStackN(int v)
	{
		stackN = v;
	}

	public void setLocalN(int v)
	{
		localN = v;
	}

	public void setIns(byte[] bs, int addrBegin, int addrEnd1)
	{
		if (bs == null)
			throw null;
		Math2.checkRange(addrBegin, addrEnd1, bs.length);
		if (addrEnd1 - addrBegin > 65535)
			throw new ClassFormatError("too large code");
		ins = bs;
		addrN = addrEnd1 - addrBegin;
		addrBi = addrBegin;
	}

	public void setIns(Instruction i, boolean clone)
	{
		if (i.addr > 65535)
			throw new ClassFormatError("too large code");
		ins = clone ? Array2.subClone(i.bytes, 0, i.addr) : i.bytes;
		addrN = i.addr;
		addrBi = 0;
	}

	@Override
	public int generateByteN()
	{
		int n = byteN();
		n += addrN - addrN0;
		if (catchs != null)
			n += catchs.generateByteN() - catchs.byteN();
		if (lines != null)
			n += lines.generateByteN() - lines.byteN();
		if (vars != null)
			n += vars.generateByteN() - vars.byteN();
		if (varSigns != null)
			n += varSigns.generateByteN() - varSigns.byteN();
		return n;
	}

	@Override
	public int generateTo(byte[] bs, int begin)
	{
		if (attrNameCi <= 0)
			throw new RuntimeException("attribute name constant index must be set");
		writeU2(bs, begin, attrNameCi);
		writeU2(bs, begin + 6, stackN);
		writeU2(bs, begin + 8, localN);

		int bi = beginBi + 10;
		int bbi = begin + 10;
		writeU4(bs, bbi, addrN);
		copyInsTo(0, bs, bbi + 4, addrN);
		bi += 4 + addrN0;
		bbi += 4 + addrN;

		if (catchs == null)
		{
			System.arraycopy(bytes, bi, bs, bbi, catchBn);
			bbi += catchBn;
		}
		else
			bbi = catchs.generateTo(bs, bbi);

		writeU2(bs, bbi, attrN);
		bi = attrBi;
		bbi += 2;
		for (int an = attrN; an > 0; an--)
		{
			int bn = 6 + read0u4(bi + 2);
			if (bi == linesBi && lines != null)
				bbi = lines.generateTo(bs, bbi);
			else if (bi == varsBi && vars != null)
				bbi = vars.generateTo(bs, bbi);
			else if (bi == varSignsBi && varSigns != null)
				bbi = varSigns.generateTo(bs, bbi);
			else
			{
				System.arraycopy(bytes, bi, bs, bbi, bn);
				bbi += bn;
			}
			bi += bn;
		}

		writeS4(bs, begin + 2, bbi - begin - 6);
		return bbi;
	}
}
