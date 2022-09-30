package me.refactoring.study._18_middle_man._40_replace_subclass_with_delegate;

import java.time.LocalDateTime;

public class PremiumBooking extends Booking {

    private PremiumExtra extra;

    public PremiumBooking(Show show, LocalDateTime time, PremiumExtra extra) {
        super(show, time);
        this.extra = extra;
    }

//    @Override
//    public boolean hasTalkback() { // 아무일도 하지 않고 위임만 한다.
//        return this.premiumDelegate.hasTalkback();
//    }

//    @Override
//    public double basePrice() {
//        return Math.round(super.basePrice() + this.extra.getPremiumFee());
//    }

}
