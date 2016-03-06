package org.codehaus.mojo.properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ReadPropertiesMojoTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MavenProject projectStub;
    private ReadPropertiesMojo readPropertiesMojo;

    @Before
    public void setUp() {
        projectStub = new MavenProject();
        readPropertiesMojo = new ReadPropertiesMojo();
        readPropertiesMojo.setProject(projectStub);
    }

    @Test
    public void readPropertiesWithoutKeyPrefix()
            throws Exception {
        readWithoutKeyPrefix(ResourceType.PROPERTIES);
    }

    @Test
    public void readYamlWithoutKeyPrefix()
            throws Exception {
        readWithoutKeyPrefix(ResourceType.YAML);
    }

    public void readWithoutKeyPrefix(final ResourceType resourceType)
            throws Exception {

        // Arrange
        final File testPropertyFile = getFileForTesting(resourceType);
        final Properties testProperties1 = getProperties(resourceType, testPropertyFile);
        final Properties testProperties2 = getProperties(resourceType, testPropertyFile);

        addFilesToReadPropertiesMojo(testPropertyFile);

        final Properties testProperties = getProperties(resourceType, testPropertyFile);
        assertNotNull(testProperties);
        assertFalse(testProperties.size() == 0);

        // Act
        readPropertiesMojo.execute();

        // Assert
        final Properties projectProperties = projectStub.getProperties();
        assertNotNull(projectProperties);
        assertFalse(projectProperties.size() == 0);

        // we are not adding prefix, so properties should be same as in file
        assertEquals(testProperties.size(), projectProperties.size());
        assertEquals(testProperties, projectProperties);

    }

    @Test
    public void readPropertiesWithKeyprefix()
            throws Exception {
        readWithKeyPrefix(ResourceType.PROPERTIES);
    }

    @Test
    public void readYamlWithKeyprefix()
            throws Exception {
        readWithKeyPrefix(ResourceType.YAML);
    }

    private void readWithKeyPrefix(final ResourceType resourceType)
            throws Exception {
        // Arrange
        final File testPropertyFileWithoutPrefix = getFileForTesting(resourceType);
        addFilesToReadPropertiesMojo(testPropertyFileWithoutPrefix);

        final Properties testPropertiesWithoutPrefix = getProperties(resourceType, testPropertyFileWithoutPrefix);
        assertNotNull(testPropertiesWithoutPrefix);
        assertFalse(testPropertiesWithoutPrefix.size() == 0);

        final String keyPrefix = "test-key-prefix.";
        final Properties testPropertiesPrefix = getProperties(resourceType, getFileForTesting(resourceType, keyPrefix));
        assertNotNull(testPropertiesPrefix);
        assertFalse(testPropertiesPrefix.size() == 0);
        readPropertiesMojo.setKeyPrefix(keyPrefix);

        // Act
        readPropertiesMojo.execute();

        // Assert
        final Properties projectProperties = projectStub.getProperties();
        assertNotNull(projectProperties);
        assertNotEquals(0, projectProperties.size());

        // we are adding prefix, so prefix properties should be same as in projectProperties
        assertEquals(testPropertiesPrefix.size(), projectProperties.size());
        assertEquals(testPropertiesPrefix, projectProperties);

        // properties with and without prefix shouldn't be same
        assertNotEquals(testPropertiesPrefix, testPropertiesWithoutPrefix);
        assertNotEquals(testPropertiesWithoutPrefix, projectProperties);
    }

    private Properties getProperties(final ResourceType resourceType, final File file)
            throws IOException, MojoExecutionException {
        final InputStream inputStream = new FileInputStream(file);
        switch (resourceType) {

            case PROPERTIES:
                return getProperties(inputStream);

            case YAML:
                return getPropertiesFromYaml(inputStream);

            default:
                fail("Unsupported resource type");
        }

        return null;
    }

    private Properties getProperties(final InputStream inputStream)
            throws IOException {
        final Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    private Properties getPropertiesFromYaml(final InputStream inputStream)
            throws IOException, MojoExecutionException {
        return YamlToPropertiesConverter.convertToProperties(inputStream);
    }

    private File getFileForTesting(final ResourceType resourceType)
            throws IOException {
        switch (resourceType) {
            case PROPERTIES:
                return getPropertyFileForTesting();

            case YAML:
                return getYamlFileForTesting();

            default:
                fail("Unsupported resource type");
        }

        return null;
    }

    private File getFileForTesting(final ResourceType resourceType, final String keyPrefix)
            throws IOException {
        switch (resourceType) {
            case PROPERTIES:
                return getPropertyFileForTesting(keyPrefix);

            case YAML:
                return getYamlFileForTesting(keyPrefix);

            default:
                fail("Unsupported resource type");
        }

        return null;
    }

    private File getPropertyFileForTesting()
            throws IOException {
        return getPropertyFileForTesting(null);
    }

    private File getPropertyFileForTesting(final String keyPrefix)
            throws IOException {
        final String prefix = keyPrefix == null ? "" : keyPrefix;
        final List<String> lines = Arrays.asList(
                prefix + "test.property1=value1",
                prefix + "test.property2=value2",
                prefix + "test.property3=value3");
        return createFile(".properties", lines);
    }

    private File getYamlFileForTesting()
            throws IOException {
        return getYamlFileForTesting(null);
    }

    private File getYamlFileForTesting(final String keyPrefix)
            throws IOException {
        final String prefix = keyPrefix == null ? "" : keyPrefix;
        final List<String> lines = Arrays.asList(
                prefix + "test:",
                "     property1: value1",
                "     property2: value2",
                "     property3: value3");
        return createFile(".yml", lines);
    }

    private File createFile(final String fileExtension, final Collection<String> lines)
            throws IOException {
        final File file = folder.newFile(UUID.randomUUID() + fileExtension);
        FileUtils.writeLines(file, lines);
        return file;
    }

    private void addFilesToReadPropertiesMojo(final File... files)
            throws NoSuchFieldException, IllegalAccessException {
        final Class<?> clazz = readPropertiesMojo.getClass();

        final Field filesField = clazz.getDeclaredField("files");
        filesField.setAccessible(true);
        filesField.set(readPropertiesMojo, files);
    }

}
