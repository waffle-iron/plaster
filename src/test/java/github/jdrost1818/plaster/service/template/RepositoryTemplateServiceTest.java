package github.jdrost1818.plaster.service.template;

import com.google.common.collect.Lists;
import github.jdrost1818.plaster.data.Setting;
import github.jdrost1818.plaster.data.StoredJavaType;
import github.jdrost1818.plaster.data.TemplateType;
import github.jdrost1818.plaster.domain.*;
import github.jdrost1818.plaster.domain.template.FlattenedField;
import github.jdrost1818.plaster.service.ConfigurationService;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepositoryTemplateServiceTest {

    @Mock
    private ConfigurationService configurationService;

    private RepositoryTemplateService classUnderTest;

    private FileInformation fileInformation;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        this.classUnderTest = new RepositoryTemplateService(configurationService);

        Field id = new Field(new TypeDeclaration("List", Lists.newArrayList(StoredJavaType.LIST.getType(false))), "id");
        Field mapField = new Field(new TypeDeclaration("Map", Lists.newArrayList(StoredJavaType.MAP.getType(false))), "var1");
        Field listField = new Field(new TypeDeclaration("List", Lists.newArrayList(StoredJavaType.LIST.getType(false))), "var2");
        Field exampleField = new Field(new TypeDeclaration("Example", Lists.newArrayList(new Type("Example", new Dependency("com.example.app.Example")))), "var3");

        this.fileInformation = new FileInformation("example_class", id, Lists.newArrayList(
                mapField, listField, exampleField
        ));
    }

    @Test
    public void addTypeField() throws Exception {
        JtwigModel model = JtwigModel.newModel();

        GenTypeModel genTypeModel = new GenTypeModel("example_class", false);

        when(this.configurationService.get(Setting.APP_PATH)).thenReturn("/com/example/app");
        when(this.configurationService.get(Setting.REL_REPOSITORY_PACKAGE)).thenReturn("/repository");
        when(this.configurationService.get(Setting.SUB_DIR_PATH)).thenReturn("/somewhere");

        JtwigModel modifiedModel = this.classUnderTest.addTypeField(model, genTypeModel, TemplateType.REPOSITORY);

        FlattenedField x = (FlattenedField) modifiedModel.get("repoField").get().getValue();

        assertThat(x.getClassName(), equalTo("ExampleClassRepository"));
        assertThat(x.getPackagePath(), equalTo("com.example.app.repository.somewhere"));
        assertThat(x.getVarName(), equalTo("exampleClassRepository"));
    }

    @Test
    public void getTemplate() {
        JtwigTemplate foundTemplate = this.classUnderTest.getTemplate();

        assertThat(foundTemplate, notNullValue());
    }

    @Test
    public void addCustomInformation() {
        JtwigModel model = JtwigModel.newModel();
        GenTypeModel genTypeModel = new GenTypeModel("example_class", true);

        JtwigModel modifiedModel = this.classUnderTest.addCustomInformation(model, this.fileInformation, genTypeModel);

        List<Dependency> dependencies = (List<Dependency>)modifiedModel.get("dependencies").get().getValue();

        assertThat(dependencies, hasSize(1));
        assertThat(modifiedModel.get("modelField").get().getValue(), equalTo(new FlattenedField("", "ExampleClass", "exampleClass")));
        assertThat(modifiedModel.get("repoField").get().getValue(), equalTo(new FlattenedField("", "ExampleClassRepository", "exampleClassRepository")));
        assertThat(modifiedModel.get("idField").get().getValue(), equalTo(new FlattenedField("", "List", "id")));
    }

    @Test
    public void renderTemplate() {
        String expected = "" +
                "package com.example.app.repository.somewhere;\n" +
                "\n" +
                "import org.springframework.data.domain.Page;\n" +
                "import org.springframework.data.domain.Pageable;\n" +
                "import org.springframework.data.jpa.domain.Specification;\n" +
                "import org.springframework.data.repository.CrudRepository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import com.example.app.model.somewhere.ExampleClass;\n" +
                "\n" +
                "public interface ExampleClassRepository extends CrudRepository<ExampleClass, List> {\n" +
                "\n" +
                "    Page<ExampleClass> findAll(Specification<ExampleClass> spec, Pageable pageInfo);\n" +
                "\n" +
                "    ExampleClass findOne(Specification<ExampleClass> spec);\n" +
                "\n" +
                "}";

        GenTypeModel genTypeModel = new GenTypeModel("ExampleClass", false);

        when(this.configurationService.get(Setting.APP_PATH)).thenReturn("/com/example/app");
        when(this.configurationService.get(Setting.REL_MODEL_PACKAGE)).thenReturn("/model");
        when(this.configurationService.get(Setting.REL_REPOSITORY_PACKAGE)).thenReturn("/repository");
        when(this.configurationService.get(Setting.REL_CONTROLLER_PACKAGE)).thenReturn("/controller");
        when(this.configurationService.get(Setting.REL_SERVICE_PACKAGE)).thenReturn("/service");
        when(this.configurationService.get(Setting.SUB_DIR_PATH)).thenReturn("/somewhere");

        String actual = this.classUnderTest.renderTemplate(this.fileInformation, genTypeModel);

        assertThat(expected, equalTo(actual));
    }
}