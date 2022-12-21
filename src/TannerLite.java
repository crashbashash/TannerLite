import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.GameType;
import com.epicbot.api.shared.script.LoopScript;
import com.epicbot.api.shared.script.ScriptManifest;
import com.epicbot.api.shared.script.ScriptTaskContainer;
import com.epicbot.api.shared.util.Random;
import com.epicbot.api.shared.util.paint.frame.PaintFrame;
import com.epicbot.api.shared.util.time.Time;
import com.google.gson.Gson;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.Locale;

@ScriptManifest(name = "Tanner Lite [ALPHA]", gameType = GameType.OS)
public class TannerLite extends LoopScript {

    public String currentTask = "Starting up...";
    public int tanned = 0;

    private ScriptTaskContainer taskContainer;

    private final long startTime = System.currentTimeMillis();

    private TannerLiteGUI gui;
    private TannerSettings settings;

    private boolean started = false;

    NumberFormat nf = NumberFormat.getInstance(Locale.UK);

    @Override
    protected int loop() {
        if (taskContainer != null && getAPIContext().client().isLoggedIn() && started) {
            taskContainer.setIterationDelay(Random.nextInt(1800, 2400));
            taskContainer.run();
        }
        return 0;
    }

    @Override
    public boolean onStart(String... strings) {
        getAPIContext().webWalking().setUseTeleports(true);
        settings = new TannerSettings();

        taskContainer = new ScriptTaskContainer(this);
        taskContainer.addTask(new TanLeather(this));
        taskContainer.addTask(new BankLeather(this));

        if (getAPIContext().script().getScriptProfile().isPresent()){
            Gson gson = new Gson();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("TannerLite.json"));
                settings = gson.fromJson(reader, TannerSettings.class);
                startBot();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        gui = new TannerLiteGUI(this);

        return true;
    }

    private double getProfit() {
        return tanned * getAPIContext().pricing().get(settings.getTannable().getTannedName()).getLowestPrice() -
                tanned * getAPIContext().pricing().get(settings.getTannable().getRawName()).getHighestPrice() -
                tanned * settings.getTannable().getTanCost();
    }

    private long getProfitPerHour() {
        return Math.round(getProfit() / ((System.currentTimeMillis() - startTime) / 3600000.0));
    }

    @Override
    public void onStop() {
        startBreak();
        gui.dispose();
    }

    @Override
    protected void onPaint(Graphics2D g, APIContext apiContext) {
        PaintFrame frame = new PaintFrame("Private Tanner [ALPHA]");
        frame.addLine("Time running", Time.getFormattedTime(System.currentTimeMillis() - startTime));
        frame.addLine("Current task", currentTask);
        if (tanned > 0) {
            frame.addLine("Tanned", nf.format(tanned));
            frame.addLine("Profit", nf.format(getProfit()) + " gp");
            frame.addLine("Profit/h", nf.format(getProfitPerHour()) + " gp");
        }
        frame.draw(g, 0, 150, apiContext);
    }

    public void startBot() {
        if (gui != null) {
            gui.dispose();
        }
        started = true;
    }

    public void stopBot(String message) {
        started = false;
        if (getAPIContext().bank().isOpen()){
            getAPIContext().bank().close();
        }

        if (getAPIContext().client().isLoggedIn()) {
            getAPIContext().game().logout();
        }

        getAPIContext().script().stop(message);
    }

    public TannerSettings getSettings() {
        return settings;
    }
}