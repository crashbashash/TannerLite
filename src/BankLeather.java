import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.entity.SceneObject;
import com.epicbot.api.shared.model.Area;
import com.epicbot.api.shared.query.result.LocatableEntityQueryResult;
import com.epicbot.api.shared.script.task.ScriptTask;
import com.epicbot.api.shared.util.Random;
import com.epicbot.api.shared.util.time.Time;

public class BankLeather implements ScriptTask {
    private final APIContext apiContext;
    private final TannerLite bot;

    private final Area bankArea = new Area(3274, 3166, 3269, 3167);
    private final int doorID = 1535;

    public BankLeather(TannerLite _bot) {
        this.apiContext = _bot.getAPIContext();
        this.bot = _bot;
    }

    @Override
    public void run() {
        bot.currentTask = "Obtaining " + bot.getSettings().getTannable().getRawName();
        if (!apiContext.bank().isOpen()) {
            if (!apiContext.bank().open()) {
                LocatableEntityQueryResult<SceneObject> doors = apiContext.objects().query().id(doorID).results();
                if (doors.size() > 0) {
                    doors.nearest().interact("Open");
                    return;
                }
                apiContext.webWalking().walkTo(bankArea.getRandomTile());
                Time.sleep(Random.nextInt(1000, 1500), Random.nextInt(4000, 5000), () -> apiContext.bank().isOpen());
                return;
            }
            Time.sleep(2000, 6000, () -> apiContext.bank().isOpen());
            return;
        }

        if (apiContext.inventory().getItem("Coins") == null || apiContext.inventory().getItem("Coins").getStackSize() < bot.getSettings().getTannable().getTanCost()) {
            if (apiContext.bank().getItem("Coins") == null || apiContext.bank().getItem("Coins").getStackSize() < bot.getSettings().getTannable().getTanCost()) {
                bot.stopBot("Out of coins");
                return;
            }
            apiContext.bank().withdrawAll("Coins");
            Time.sleep(1000, 3000, () -> apiContext.inventory().getItem("Coins") != null);
            return;
        }

        if (apiContext.bank().getCount(bot.getSettings().getTannable().getRawName()) < 1) {
            bot.stopBot("Out of " + bot.getSettings().getTannable().getRawName());
            return;
        }
        apiContext.bank().depositAllExcept("Coins");
        Time.sleep(1000, 3000, () -> apiContext.inventory().getCount(bot.getSettings().getTannable().getTannedName()) <= 0);
        apiContext.bank().withdrawAll(bot.getSettings().getTannable().getRawName());
        Time.sleep(1000, 3000, () -> apiContext.inventory().contains(bot.getSettings().getTannable().getRawName()));
        apiContext.bank().close();
        Time.sleep(1000, 3000, () -> !apiContext.bank().isOpen());
    }

    @Override
    public boolean shouldExecute() {
        return !apiContext.inventory().contains(bot.getSettings().getTannable().getRawName());
    }
}
