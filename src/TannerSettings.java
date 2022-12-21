public class TannerSettings {
    private Tannables tannable = Tannables.SOFT_LEATHER;

    public TannerSettings() {
    }

    public Tannables getTannable() {
        return tannable;
    }

    public void setTannable(Tannables tannable) {
        this.tannable = tannable;
    }
}
