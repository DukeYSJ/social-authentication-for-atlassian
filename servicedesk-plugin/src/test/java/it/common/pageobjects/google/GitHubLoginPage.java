package it.common.pageobjects.google;

import com.atlassian.jira.pageobjects.pages.AbstractJiraPage;
import com.atlassian.pageobjects.elements.ElementBy;
import com.atlassian.pageobjects.elements.PageElement;
import com.atlassian.pageobjects.elements.query.TimedCondition;

public class GitHubLoginPage extends AbstractJiraPage {
    @ElementBy(id = "login_field")
    PageElement email;

    @ElementBy(id = "password")
    PageElement password;

    @ElementBy(name = "commit")
    PageElement signInButton;

    @Override
    public TimedCondition isAt() {
        return password.timed().isVisible();
    }

    @Override
    public String getUrl() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmailVisible() {
        return this.email.isVisible();
    }

    public GitHubLoginPage setEmail(String email) {
        this.email.clear();
        this.email.type(email);
        return this;
    }

    public GitHubLoginPage setPassword(String password) {
        this.password.clear();
        this.password.type(password);
        return this;
    }

    public TimedCondition isSignInEnabled()
    {
        return signInButton.timed().isEnabled();
    }

    public com.atlassian.pageobjects.DelayedBinder<GitHubApprovePage> signIn() {
        this.signInButton.click();
        return pageBinder.delayedBind(GitHubApprovePage.class);
    }
}
