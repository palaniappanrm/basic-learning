import java.util.List;

public class GitCommit {

    private String branch = "master";
    private String commit_message = "from superadmin API";
    private List<GitCommitAction> actions;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCommit_message() {
        return commit_message;
    }

    public void setCommit_message(String commit_message) {
        this.commit_message = commit_message;
    }

    public List<GitCommitAction> getActions() {
        return actions;
    }

    public void setActions(List<GitCommitAction> actions) {
        this.actions = actions;
    }
}
