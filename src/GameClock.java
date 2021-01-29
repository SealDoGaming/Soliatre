import java.util.TimerTask;

public class GameClock extends TimerTask {
	private Soliatre display;
	
	public GameClock(Soliatre display) {
		this.display=display;
	}
    @Override
    public void run() {
        display.runtime();
    }

}
