package command;

import static constants.UserGuideMessages.USER_GUIDE_INFORMATION;

import ui.Ui;

/**
 * Class to print user guide information
 */
public class CommandUserGuide extends Command{

    @Override
    public void executeCommand() {
        Ui.showToUser(USER_GUIDE_INFORMATION);
    }
}
