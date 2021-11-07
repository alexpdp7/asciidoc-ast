package net.pdp7.asciidoc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.asciidoc.intellij.lexer.AsciiDocTokenTypes;
import org.asciidoc.intellij.parser.AsciiDocElementTypes;

import com.intellij.psi.tree.IElementType;

public class DebugHelpers {

	public static void main(String[] args) {
		System.out.println(getAllTypes(AsciiDocTokenTypes.class));
		System.out.println(getAllTypes(AsciiDocElementTypes.class));
	}

	public static String getAllTypes(Class<?> clazz) {
		return Arrays.stream(clazz.getFields()).filter(f -> f.getType().equals(IElementType.class)).map(DebugHelpers::typeToName).collect(Collectors.joining(", "));
	}
	
	public static String typeToName(Field field) {
		try {
			return ((IElementType) field.get(null)).getDebugName();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
