public enum Tannables {
    SOFT_LEATHER("Leather", "Cowhide", 1, 100),
    HARD_LEATHER("Hard leather", "Cowhide", 3, 101),
    SNAKE_SKIN("Snakeskin", "Snake hide", 20, 102),
    GREEN_DRAGON_HIDE("Green dragon leather", "Green dragonhide", 20, 104),
    BLUE_DRAGON_HIDE("Blue dragon leather", "Blue dragonhide", 20, 105),
    RED_DRAGON_HIDE("Red dragon leather", "Red dragonhide", 20, 106),
    BLACK_DRAGON_HIDE("Black dragon leather", "Black dragonhide", 20, 107);

    private final String tannedName;
    private final String rawName;
    private final int tanCost;
    private final int widgetChild;

    Tannables(String tannedName, String rawName, int tanCost, int widgetChild) {
        this.tannedName = tannedName;
        this.rawName = rawName;
        this.tanCost = tanCost;
        this.widgetChild = widgetChild;
    }

    public String getTannedName() {
        return tannedName;
    }

    public String getRawName() {
        return rawName;
    }

    public int getTanCost() {
        return tanCost;
    }

    public int getWidgetChild() {
        return widgetChild;
    }
}
