package org.codehaus.mojo.properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.yaml.snakeyaml.composer.ComposerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class YamlToPropertiesConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File file;

    @Before
    public void setUp()
            throws Exception {
        file = folder.newFile("test.yml");
    }

    @Test
    public void testConvertToProperties()
            throws Exception {
        // Arrange
        final List<String> lines = Arrays.asList(
                "---",
                "this.is.a.standard.property: this is the value",
                "a: #This comment shouldn't appear",
                "   hierarchical:",
                "       property1: yet another value",
                "       property2: the last value",
                "list:",
                "   -list value1",
                "   -list value2", "anotherkey:",
                "    { nestedkey1: value1, nestedkey2: value2 }",
                "list2:",
                "   - map_key: map_value",
                "empty_key:",
                "...");
        FileUtils.writeLines(file, lines);

        final InputStream inputStream = new FileInputStream(file);

        // Act
        final Properties properties = YamlToPropertiesConverter.convertToProperties(inputStream);

        // Assert
        assertEquals("Eight keys are expected", properties.keySet().size(), 8);

        assertTrue(properties.keySet().contains("this.is.a.standard.property"));
        assertTrue(properties.getProperty("this.is.a.standard.property").equals("this is the value"));

        assertTrue(properties.keySet().contains("a.hierarchical.property1"));
        assertTrue(properties.getProperty("a.hierarchical.property1").equals("yet another value"));
        assertTrue(properties.keySet().contains("a.hierarchical.property2"));
        assertTrue(properties.getProperty("a.hierarchical.property2").equals("the last value"));

        assertTrue(properties.keySet().contains("list"));
        assertTrue(properties.getProperty("list").equals("-list value1 -list value2"));

        assertTrue(properties.keySet().contains("anotherkey.nestedkey1"));
        assertTrue(properties.getProperty("anotherkey.nestedkey1").equals("value1"));
        assertTrue(properties.keySet().contains("anotherkey.nestedkey2"));
        assertTrue(properties.getProperty("anotherkey.nestedkey2").equals("value2"));
        assertTrue(properties.keySet().contains("list2.map_key"));
        assertTrue(properties.getProperty("list2.map_key").equals("map_value"));
        assertTrue(properties.getProperty("empty_key").equals(""));
    }

    @Test
    public void testConvertToPropertiesIgnoreComments()
            throws Exception {
        // Arrange
        final List<String> lines = Arrays.asList(
                "this.is.a.standard.property: this is the value",
                "#A very helpful comment",
                "a:",
                "   hierarchical:",
                "       property: yet another value");
        FileUtils.writeLines(file, lines);

        final InputStream inputStream = new FileInputStream(file);

        // Act
        final Properties properties = YamlToPropertiesConverter.convertToProperties(inputStream);

        // Assert
        assertEquals("Two keys are expected", properties.keySet().size(), 2);

        assertTrue(properties.keySet().contains("this.is.a.standard.property"));
        assertTrue(properties.getProperty("this.is.a.standard.property").equals("this is the value"));

        assertTrue(properties.keySet().contains("a.hierarchical.property"));
        assertTrue(properties.getProperty("a.hierarchical.property").equals("yet another value"));
    }

    @Test
    public void testConvertToPropertiesFailsOnNewDocument()
            throws Exception {
        // Arrange
        thrown.expect(ComposerException.class);

        final List<String> lines = Arrays.asList(
                "---",
                "this.is.a.standard.property: this is the value",
                "a:",
                "...",
                "---",
                "   hierarchical:",
                "       property: yet another value",
                "...");
        FileUtils.writeLines(file, lines);

        final InputStream inputStream = new FileInputStream(file);

        // Act
        YamlToPropertiesConverter.convertToProperties(inputStream);

        // Assert
        fail("Expected an exception because of multiple documents in the same Yaml file");
    }

}
