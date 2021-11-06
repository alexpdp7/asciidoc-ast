package net.pdp7.asciidoc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.asciidoc.intellij.AsciiDocLanguage;
import org.asciidoc.intellij.parser.AsciiDocParserDefinition;
import org.asciidoc.intellij.psi.AsciiDocASTFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageASTFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ParsingTestCase;

public class App {

	public static void main(String[] args) throws Exception {
		Path inPath = Path.of(args[0]);
		String inAdoc = Files.readString(inPath);

		ASTNode ast = parse(inAdoc);

		GsonBuilder gson = new GsonBuilder();
		String json = gson.create().toJson(toJsonElement(ast));

		if (!args[0].endsWith(".adoc")) {
			throw new Exception(args[0] + " does not end in .adoc");
		}
		Path outPath = Path.of(args[0].replaceFirst("\\.adoc", ".ast.json"));
		Files.writeString(outPath, json);
	}

	private static JsonElement toJsonElement(ASTNode astNode) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", astNode.getElementType().toString());
		if (astNode.getChildren(null).length == 0) {
			jsonObject.addProperty("text", astNode.getText());
		}
		TextRange textRange = astNode.getTextRange();
		jsonObject.addProperty("startOffset", textRange.getStartOffset());
		jsonObject.addProperty("endOffset", textRange.getEndOffset());

		JsonArray children = new JsonArray();

		Arrays.stream(astNode.getChildren(null)).map(n -> toJsonElement(n)).forEach(e -> children.add(e));

		jsonObject.add("children", children);

		return jsonObject;

	}

	public static ASTNode parse(String asciiDoc) {
		// silence noisy IntelliJ code
		PrintStream stderr = System.err;
		try {
			System.setErr(new PrintStream(new ByteArrayOutputStream()));
	
			TestAdapter testAdapter = new TestAdapter();
			try {
				testAdapter.setUp();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			PsiFile psiFile = testAdapter.doParseFile("foo", asciiDoc);
			return psiFile.getNode();
		}
		finally {
			System.setErr(stderr);
		}
	}

	public static class TestAdapter extends ParsingTestCase {
		public TestAdapter() {
			super("parser", "adoc", true, new AsciiDocParserDefinition());
		}

		@Override
		protected void setUp() throws Exception {
			super.setUp();
			addExplicitExtension(LanguageASTFactory.INSTANCE, AsciiDocLanguage.INSTANCE, new AsciiDocASTFactory());
		}

		@Override
		protected String getTestDataPath() {
			return "foo";
		}

		public PsiFile doParseFile(String name, String text) {
			return parseFile(name, text);
		}

	}
}
