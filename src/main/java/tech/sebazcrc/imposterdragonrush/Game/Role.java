package tech.sebazcrc.imposterdragonrush.Game;

public enum Role {

    NOT_DEFINED("-", "-", "-"), IMPOSTER("&c&l¡IMPOSTOR!", "Engaña y elimina a TODOS", "&c&lImpostor"), INNOCENT("&a&l¡INOCENTE!", "Sobrevive y recolecta materiales", "&b&lInocente");

    private String titleName;
    private String subTitleName;
    private String scoreName;

    Role (String titleName, String subTitleLore, String scoreName) {
        this.titleName = titleName;
        this.subTitleName = subTitleLore;
        this.scoreName = scoreName;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getSubTitleName() {
        return subTitleName;
    }

    public String getScoreName() {
        return scoreName;
    }
}
