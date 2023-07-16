package eu.stamp_project.descartes.reporting;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.networknt.schema.*;
import eu.stamp_project.test.InMemoryReportStrategy;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

public abstract class JsonListenerTest<TFactory extends MutationResultListenerFactory> extends ListenerTest<TFactory> {

  JsonListenerTest(Class<TFactory> factoryClass) {
    super(factoryClass);
  }

  abstract String getFileName();

  @ParameterizedTest
  @MethodSource("provideClassMutationResults")
  void testReportConformsSchema(Collection<ClassMutationResults> results)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException {

    InMemoryReportStrategy reportStrategy = new InMemoryReportStrategy();
    MutationResultListener listener = getListener(reportStrategy);
    handleMutationResults(listener, results);

    String fileName = getFileName();
    final String reportContent = reportStrategy.getContentForFile(fileName + ".json");
    final String schemaDefinitionLocation = "schemas/" + fileName + ".report.schema.json";
    InputStream schemaDefinition = MethodTestingListener.class.getClassLoader().getResourceAsStream(schemaDefinitionLocation);
    assertNotNull(schemaDefinition, "Schema definition not found at: " + schemaDefinitionLocation);
    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    JsonSchema jsonSchema = jsonSchemaFactory.getSchema(schemaDefinition);
    Set<ValidationMessage> validationMessages = jsonSchema.validate(reportContent, InputFormat.JSON);
    assertTrue(
        validationMessages.isEmpty(),
        // Generate message
        () -> "Generated report does not conform to schema: " +
            schemaDefinitionLocation + "\n" +
            validationMessages.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining("\n"))
    );
  }


}
