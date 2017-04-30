package edu.spbpu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Builder
@Value
public class Inverter implements Serializable {
    private static final long serialVersionUID = -1473992963453368125L;
    private final Long id;
    private final String name;
    private final Double P_ac0;
    private final Double P_dc0;
    private final Double V_dc0;
    private final Double V_dcmax;
    private final Double I_dcmax;
    private final Double V_ac0;
    private final Double P_s0;
    private final Double C0;
    private final Double C1;
    private final Double C2;
    private final Double C3;
    private final Double P_nt;
    private final Double Mppt_low;
    private final Double Mppt_high;
    private int connections;

    @Value
    @Builder
    @AllArgsConstructor
    @Deprecated
    static class Operation {
        private final double P_dc;
        private final double P_ac;
        private final double V_dc;
        private final double I_dc;
        private final double load;
        private final double efficiency;
    }

    @SuppressWarnings("WeakerAccess")
    public double calculateACPower(double P_dc, double V_dc) {
        if (V_dc < Mppt_low) return 0;
        if (V_dc > Mppt_high) {
            P_dc = P_dc / V_dc * Mppt_high;
            V_dc = Mppt_high;
        }
        double A = P_dc0 * (1 + C1 * (V_dc - V_dc0));
        double B = P_s0 * (1 + C2 * (V_dc - V_dc0));
        double C = C0 * (1 + C3 * (V_dc - V_dc0));
        return (P_ac0 / (A + B) - C * (A - B)) * (P_dc - B) + C * Math.pow(P_dc - B, 2);
    }

    public static void main(String[] args) {
        Inverter inverter = Inverter.builder()
                .id(1L)
                .name("ABB: MICRO-0.25-I-OUTD-US-208 208V [CEC 2014]")
                .V_ac0(208.0)
                .P_ac0(250.0)
                .P_dc0(259.52)
                .V_dc0(40.2426)
                .P_s0(1.7716)
                .C0(-2.48E-5)
                .C1(-9.01E-5)
                .C2(6.69E-4)
                .C3(-1.89E-2)
                .P_nt(0.02)
                .V_dcmax(65.0)
                .I_dcmax(10.0)
                .build();
        for (double P_dc = 0; P_dc < inverter.P_dc0; P_dc += inverter.P_dc0 / 50)
            System.out.println("DC:" + P_dc + " " + inverter.calculateACPower(P_dc, inverter.V_dc0));
    }
}