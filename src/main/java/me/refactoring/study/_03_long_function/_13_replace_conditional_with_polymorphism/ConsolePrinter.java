package me.refactoring.study._03_long_function._13_replace_conditional_with_polymorphism;

import java.io.IOException;
import java.util.List;

public class ConsolePrinter extends StudyPrinter{

    public ConsolePrinter(int totalNumberOfEvents, List<Participant> participants, PrinterMode printerMode) {
        super(totalNumberOfEvents, participants, printerMode);
    }

    @Override
    public void execute() throws IOException {
        this.participants.forEach(p -> {
            System.out.printf("%s %s:%s\n", p.username(), checkMark(p), p.getRate(this.totalNumberOfEvents));
        });
    }


}