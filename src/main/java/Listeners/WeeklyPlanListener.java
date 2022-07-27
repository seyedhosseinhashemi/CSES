package Listeners;

import ClientSide.ClientReqType;
import ClientSide.DataHandler;
import Pages.GuiController;
import Pages.PanelType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class WeeklyPlanListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        DataHandler.getInstance().updateUserLessons();
        GuiController.getInstance().changePanelTo(PanelType.WEEKLYPLANPAGE);

    }
}
