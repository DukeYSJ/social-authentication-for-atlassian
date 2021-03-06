package it.common;

import com.atlassian.pageobjects.elements.query.Poller;
import it.jira.pageobjects.AddProviderPage;
import it.jira.pageobjects.ConfigurationPage;
import it.jira.pageobjects.EditProviderPage;
import lombok.AllArgsConstructor;
import org.hamcrest.collection.IsIterableWithSize;

import static com.atlassian.pageobjects.elements.query.Poller.waitUntil;
import static com.atlassian.pageobjects.elements.query.Poller.waitUntilFalse;
import static it.jira.pageobjects.AddProviderPage.hasErrorMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

@AllArgsConstructor
public class AddProviderAssertions {
    AddProviderPage addPage;

    public void testAddOAuthErrors() {
        addPage.setProviderType("OpenID Connect/OAuth 2.0");

        waitUntil(addPage.getCallbackUrl(), startsWith("http://"));

        assertThat(addPage.getFormError("name"), hasErrorMessage("Please provide the name."));
        assertThat(addPage.getFormError("endpointUrl"), hasErrorMessage("Please provide the provider URL."));
        assertThat(addPage.getFormError("clientId"), hasErrorMessage("Please provide the client ID."));
        assertThat(addPage.getFormError("clientSecret"), hasErrorMessage("Please provide the client secret."));

        addPage.setEndpointUrl("https://accounts.google.com");

        waitUntil(addPage.getFormErrors(), IsIterableWithSize.<AddProviderPage.AuiErrorMessage>iterableWithSize(3));
        assertThat(addPage.getFormError("name"), hasErrorMessage("Please provide the name."));
        assertThat(addPage.getFormError("clientId"), hasErrorMessage("Please provide the client ID."));
        assertThat(addPage.getFormError("clientSecret"), hasErrorMessage("Please provide the client secret."));

        addPage.setEndpointUrl("https://wp.pl");
        addPage.setName("Testing").setClientId("XXX").setClientSecret("XXX");

        addPage = addPage.saveWithErrors();
        Poller.waitUntilTrue(addPage.hasErrors());
        waitUntil(addPage.getFormErrors(), IsIterableWithSize.<AddProviderPage.AuiErrorMessage>iterableWithSize(1));
        assertThat(addPage.getFormError("endpointUrl"), hasErrorMessage("OpenId Connect discovery document at https://wp.pl/.well-known/openid-configuration is invalid or missing."));
    }

    public void testAddAndEdit() {
        final String name = "Testing";
        final String endpointUrl = "http://asdkasjdkald.pl";

        addPage.setProviderType("OpenID 1.0");
        addPage.setName(name);
        addPage.setEndpointUrl(endpointUrl);

        waitUntil(addPage.getExtensionNamespace(), equalTo("ext1"));

        ConfigurationPage configurationPage = addPage.save();

        EditProviderPage editPage = configurationPage.editProvider("Testing");
        waitUntilFalse(editPage.isSelectProviderTypeVisible());
        waitUntil(editPage.getName(), equalTo(name));
        waitUntil(editPage.getEndpointUrl(), equalTo(endpointUrl));

        editPage.setName("");
        editPage.setEndpointUrl("");

        Poller.waitUntilTrue(editPage.hasErrors());
        assertThat(editPage.getFormError("name"), hasErrorMessage("Please provide the name."));
        assertThat(editPage.getFormError("endpointUrl"), hasErrorMessage("Please provide the provider URL."));
    }

}
