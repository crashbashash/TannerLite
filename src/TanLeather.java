import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.entity.NPC;
import com.epicbot.api.shared.entity.SceneObject;
import com.epicbot.api.shared.model.Area;
import com.epicbot.api.shared.query.result.LocatableEntityQueryResult;
import com.epicbot.api.shared.script.task.ScriptTask;
import com.epicbot.api.shared.util.Random;
import com.epicbot.api.shared.util.time.Time;

public class TanLeather implements ScriptTask {
    private final APIContext apiContext;
    private final TannerLite bot;
    private final Area tannerLocation = new Area(3272, 3191, 3274, 3192);
    private final int tannerID = 3231;
    private final int doorID = 1535;

    public TanLeather(TannerLite _bot) {
        this.apiContext = _bot.getAPIContext();
        this.bot = _bot;
    }

    @Override
    public void run() {
        bot.currentTask = "Tanning " + bot.getSettings().getTannable().getTannedName();
        if (apiContext.bank().isOpen()) {
            apiContext.bank().close();
            return;
        }

        LocatableEntityQueryResult<NPC> tanner = apiContext.npcs().query().id(tannerID).results();
        if (tanner.size() == 0) {
            apiContext.webWalking().walkTo(tannerLocation.getRandomTile());
            return;
        }

        NPC tannerNPC = tanner.first();
        if (!tannerNPC.canReach(apiContext)) {
            LocatableEntityQueryResult<SceneObject> doors = apiContext.objects().query().id(doorID).results();
            if (doors.size() > 0) {
                doors.nearest().interact("Open");
                return;
            }
        }

        if (!apiContext.widgets().get(324, bot.getSettings().getTannable().getWidgetChild()).isVisible()) {
            tannerNPC.interact("Trade");
            Time.sleep(1000, 5000, () -> apiContext.widgets().get(324, bot.getSettings().getTannable().getWidgetChild()).isVisible());
            return;
        }

        if (!apiContext.menu().isOpen()) {
            apiContext.widgets().get(324, bot.getSettings().getTannable().getWidgetChild()).interact(true);
            Time.sleep(1000, 5000, () -> apiContext.menu().isOpen());
            return;
        }

        apiContext.menu().interact("Tan All");
        Time.sleep(Random.nextInt(1000, 1500), Random.nextInt(4000, 5000), () -> apiContext.inventory().contains(bot.getSettings().getTannable().getTannedName()));
        bot.tanned += apiContext.inventory().getCount(bot.getSettings().getTannable().getTannedName());
    }

    @Override
    public boolean shouldExecute() {
        return apiContext.inventory().contains(bot.getSettings().getTannable().getRawName());
    }
}
