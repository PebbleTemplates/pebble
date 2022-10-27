package io.pebbletemplates.pebble.template.tests;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pebbletemplates.pebble.PebbleEngine;

/**
 * Used by Pebble Template Tests to simply the test code and therefore 
 * make it easier to understand what is tested by each test.
 * 
 *  A separate instance of this class should be instantiated for each
 *  template test, i.e. for each instance of a template. 
 * 
 * @author nathanward
 */
public class PebbleTestContext {

	private final Logger logger = LoggerFactory.getLogger(PebbleTestContext.class);
	
	/**
	 * The path relative to the project root directory where template files and 
	 * expected output files are stored for test purposes.
	 */
	private String testFileBasePath = "src/test/resources/template-tests";
	
	/**
	 * The Pebble template context to be used as input for a Pebble Template. 
	 */
	private Map<String, Object> templateContext = null;

	/**
	 * Whether or not the Pebble Engine instantiated will 
	 * enable New Line Trimming on the Builder when this class instantiates
	 * a Pebble Engine instance. Defaults to <code>true</code>.
	 */
	private boolean newLineTrimming = true;
	
	/**
	 * Initialize the Pebble Template Context.
	 */ 
	public PebbleTestContext() {
		this.templateContext = new HashMap<String, Object>();
	}
	
	public void setNewLineTrimming(boolean b) {
		this.newLineTrimming = b;
	}
	
	/**
	 * Put an object into the Pebble template context to be used as input for
	 * when the template is executed. One or more items can be put into the template
	 * context.
	 * 
	 * @param name Template input/parameter name (i.e. key) for use within the template
	 * @param value The object that will be referred to by the given name in the template
	 */
	public void setTemplateInput(String name, Object value) {
		this.templateContext.put(name, value);
	}

	/**
	 * Load the specified template file and execute the template using a 
	 * Pebble Engine using the default Builder (classpath and file builder). 
	 * 
	 * @param templateFilename The template filename relative to the Test File Base Path
	 * 
	 * @return The output of the template as a string.
	 * 
	 * @throws IOException Thrown if the template file is not found. 
	 */
	public String executeTemplateFromFile(String templateFilename) throws IOException {
		PebbleEngine pebbleEngine = new PebbleEngine.Builder()
				.newLineTrimming(this.newLineTrimming).strictVariables(true).build();
		return this.executeTemplateFromFile(templateFilename, pebbleEngine);
	}
	
	/**
	 * Execute a template given a template file and a Pebble Engine instance. 
	 * 
	 * @param templateFilename The template filename relative to the Test File Base Path
	 * @param pebbleEngine
	 * @return
	 * @throws IOException
	 */
	public String executeTemplateFromFile(String templateFilename, PebbleEngine pebbleEngine) throws IOException {
		Path path = Paths.get(this.testFileBasePath, templateFilename);
		logger.debug("Executing template file: {}", path.toString());
		return this.executeTemplate(path.toAbsolutePath().toString(), pebbleEngine);
	}
		
	/**
	 * Load the specified template file and execute the template using a 
	 * Pebble Engine using the default Builder (classpath and file builder). 
	 * 
	 * @param templateString The template content as a string
	 * 
	 * @return The output of the template as a string.
	 * 
	 * @throws IOException Thrown if the template file is not found. 
	 */
	public String executeTemplateFromString(String templateString) throws IOException {
		PebbleEngine pebbleEngine = new PebbleEngine.Builder().loader(new StringLoader())
				.newLineTrimming(this.newLineTrimming).build();
		return this.executeTemplate(templateString, pebbleEngine);
	}
	
	/**
	 * Load the specified template file and execute the template using the template input
	 * that has previously been specified using the setTemplateInput() method. 
	 * 
	 * @param templateFilename The template filename relative to the Test File Base Path
	 * @param pebbleEngine The Pebble Engine to be used to execute the template
	 * @return The output of the template as a string.
	 * 
	 * @throws IOException Thrown if the template file is not found. 
	 */
	public String executeTemplate(String templateName, PebbleEngine pebbleEngine) throws IOException {
		PebbleTemplate template = pebbleEngine.getTemplate(templateName);
		Writer writer = new StringWriter();
		template.evaluate(writer, this.templateContext);
		String templateOutput = writer.toString();
		logger.debug("Template Output:\n{}", templateOutput);
		return templateOutput;
	}
	
	/**
	 * Get the Expected Output content for the given filename so that the 
	 * base path to the expected template output file does not have to be
	 * specified in the actual test code. 
	 * 
	 * @param filename The name of the file that contains the expected template output. 
	 * @return The content of the expected template output file as a string.
	 * @throws IOException Thrown if the file by the given name is not found.
	 */
	public String getExpectedOutput(String filename) throws IOException {
		Path path = Paths.get(this.testFileBasePath, filename);
	    logger.debug("Expected template output file: {}", path.toAbsolutePath());
	    String expectedOutput = FileUtils.readFileToString(path.toFile(), "UTF-8");
	    return expectedOutput;
	}
	
}
