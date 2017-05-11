package github.jdrost1818.plaster.service;

import com.google.common.collect.Lists;
import github.jdrost1818.plaster.data.Setting;
import github.jdrost1818.plaster.exception.PlasterException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DependencyServiceTest {

    @Mock
    ConfigurationService configurationService;

    @Mock
    SearchService searchService;

    DependencyService classUnderTest = ServiceProvider.getDependencyService();

    @Before
    public void setUp() {
        initMocks(this);

        this.classUnderTest.setConfigurationService(this.configurationService);
        this.classUnderTest.setSearchService(this.searchService);
    }

    @Test
    public void fetchCustomDependencies_null() throws Exception {
        assertThat(this.classUnderTest.fetchDependency(null), equalTo(null));
    }

    @Test
    public void fetchCustomDependencies_empty() throws Exception {
        assertThat(this.classUnderTest.fetchDependency(""), equalTo(null));
    }

    @Test
    public void fetchCustomDependencies_stored_no_deps() throws Exception {
        assertThat(this.classUnderTest.fetchDependency("int"), equalTo(null));
    }

    @Test
    public void fetchCustomDependencies_stored() throws Exception {
        assertThat(this.classUnderTest.fetchDependency("List").getPath(), equalTo("java.util.List"));
    }

    @Test(expected = PlasterException.class)
    public void fetchCustomDependencies_custom_too_many() throws Exception {
        String customClassName = "Something";
        String className = String.format("Map<List<Integer>, %s>", customClassName);

        when(this.searchService.findClassesWithName(customClassName)).thenReturn(Lists.newArrayList("", ""));

        this.classUnderTest.fetchDependency(className);
    }

    @Test(expected = PlasterException.class)
    public void fetchCustomDependencies_custom_not_found() throws Exception {
        String customClassName = "Something";
        String className = String.format("Map<List<Integer>, %s>", customClassName);

        when(this.searchService.findClassesWithName(customClassName)).thenReturn(Lists.newArrayList());

        this.classUnderTest.fetchDependency(className);
    }

}