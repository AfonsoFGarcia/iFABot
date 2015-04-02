import cern.ais.gridwars.Coordinates;
import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.List;
import java.util.Random;

/**
 * Created by agfrg on 01/04/15.
 */
public class FABot implements PlayerBot {
    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        List<Coordinates> myCoord = universeView.getMyCells();

        for(Coordinates coord : myCoord) {
            if(universeView.getPopulation(coord) >= 2) {
                Long movement = universeView.getPopulation(coord) / 2;
                movementCommands.add(new MovementCommand(coord, getRandomDirection(), movement));
            }
        }
    }

    private MovementCommand.Direction getRandomDirection() {
        Random rand = new Random();
        switch (rand.nextInt() % 4) {
            case 0: return MovementCommand.Direction.DOWN;
            case 1: return MovementCommand.Direction.LEFT;
            case 2: return MovementCommand.Direction.UP;
            case 3: return MovementCommand.Direction.RIGHT;
            default: return MovementCommand.Direction.RIGHT;

        }
    }
}
