package github.jdrost1818.plaster.service.template;

import github.jdrost1818.plaster.data.TemplateType;
import github.jdrost1818.plaster.domain.FileInformation;
import github.jdrost1818.plaster.domain.GenTypeModel;
import github.jdrost1818.plaster.service.ConfigurationService;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class RepositoryTemplateService extends TemplateService {

    public RepositoryTemplateService(ConfigurationService configurationService) {
        super(configurationService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtwigModel addCustomInformation(JtwigModel model, FileInformation fileInformation, GenTypeModel genTypeModel) {
        model = super.addTypeField(model, genTypeModel, TemplateType.REPOSITORY);
        model = super.addTypeField(model, genTypeModel, TemplateType.MODEL);
        model = super.addDependencies(model, fileInformation.getId());
        model = super.addId(model, fileInformation);

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtwigTemplate getTemplate() {
        return JtwigTemplate.classpathTemplate("template/repository/repository.twig");
    }

}
