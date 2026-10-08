package co.istad;

import java.math.BigDecimal;

public record Money (
        BigDecimal amount
){

    public void isGraderThanZero(){
        if(amount.compareTo(BigDecimal.ZERO)>0){
            System.out.println("You can buy candy");
        }
        else {
            System.out.println("Cannot bro!!");
        }
    }
}
