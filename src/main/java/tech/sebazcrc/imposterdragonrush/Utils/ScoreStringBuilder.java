package tech.sebazcrc.imposterdragonrush.Utils;

public class ScoreStringBuilder {

    private String currentBuild;
    private boolean signature;

    public ScoreStringBuilder(boolean signature) {
        this.currentBuild = " ";
        this.signature = signature;
    }

    public ScoreStringBuilder add(String s) {
        this.currentBuild = currentBuild + "\n" + s;
        return this;
    }

    public ScoreStringBuilder space() {
        return add(" ");
    }

    public String build() {
        if (signature) add("&eeventos.sebazcrc.tk");
        return this.currentBuild;
    }
}
