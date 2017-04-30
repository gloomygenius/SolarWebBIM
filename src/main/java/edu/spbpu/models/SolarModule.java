package edu.spbpu.models;

import edu.spbpu.util.LineChartNumber;
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.ejml.simple.SimpleMatrix;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
@Log4j
public class SolarModule implements Serializable {
    private static final long serialVersionUID = -3910975642518738721L;
    private long id;
    private String name;
    private Double width;   //ширина
    private Double length; //длина
    private Double area; // площадь поверхности
    private Double scCurrentRef; //ток короткого замыкания
    private Double ocVoltageRef; //напряжение холостого хода
    private Double I_mp_REF; //ток при максимальной мощности
    private Double V_mp_REF; //напряжение при максимальной мощности
    private Double I_L_REF; //световой ток
    private Double I_0_REF; //обратный ток насыщения при стандартных условиях
    private Double alphaSC; //температурный коэффициент тока короткого замыкания
    private Double bettaOC; //Темепратурный коэффициент холостого хода
    private Double Rs_REF; //Сопротивление серии
    private Double Rsh_REF; //Сопротивление шунта
    @Builder.Default
    private Double maxCableVoltage = 1000D; //максимальное допустимое напряжение
    @Builder.Default
    private Double epsilon = 1.12; // ширина запрещённой зоны, стандартное значение для кремния
    private Integer Ns;  // количество ячеек в панели
    @Builder.Default
    private Double Tс_REF = 25D; //температура ячеек при стандартных условиях
    @Builder.Default
    private Double Geff_REF = 1000D; //излучение при стандартных условиях (согласно спецификации панели)
    @Builder.Default
    private Double Gnoct = 800D;
    private Double a_REF; // коэффициент качества
    private Double Tnoct;
    @Deprecated
    private Map<ZonedDateTime, Operation> operations; //кэш Operation

    public double getHorizontalSize(Battery.Orientation orientation) {
        if (orientation == Battery.Orientation.HORIZONTAL) return length;
        else return width;
    }

    @Builder
    @ToString
    @Value
    public static class Operation {
        public final static Operation NIGHT = new Operation(0, 0, 0, 0, 0, 0, 0, 0);
        private final double scCurrent;
        private final double ocVoltage;
        private final double mpCurrent;
        private final double mpVolatage;
        private final double maxPower;
        private final double Ta;
        private final double Geff;
        private final double windSpeed;

    }

    public Operation getOperation(double Ta, double Gb, double Gd, double windSpeed, SunPosition sunPosition, double betta) {
        if (I_L_REF == null || I_0_REF == null || a_REF == null || Rs_REF == null || Rsh_REF == null)
            initParams();
        Ta -= 273.3; //приводим к Цельсию
        Gb = Gb / sin(sunPosition.getElevation() * PI / 180.0);
        double Gmax = 1020; //максимальное излучение
        if (Gb > Gmax) Gb = Gmax;
        Operation.OperationBuilder builder = Operation.builder();
        builder.Ta(Ta);
        builder.windSpeed(windSpeed);
        if (sunPosition.getElevation() < 4 || sunPosition.getElevation() > 176 || Gb <= 0)
            return Operation.NIGHT;
        double Geff = calculateGb_eff(Gb, sunPosition, betta) + Gd * (1 + cos(betta * PI / 180)) / 2;
        if (Geff <= 0) return Operation.NIGHT;
        builder.Geff(Geff);
        double Tc = calculateTc(Ta, Geff, windSpeed);
        double I_L = Geff / Geff_REF * (I_L_REF + alphaSC * (Tc - Tс_REF));
        builder.scCurrent(I_L / (1 + Rs_REF / Rsh_REF));
        double I_mp = Geff / Geff_REF * I_mp_REF;
        builder.mpCurrent(I_mp);
        double V_mp = V_mp_REF + bettaOC * (Tc - Tс_REF);
        builder.mpVolatage(V_mp);
        builder.maxPower(V_mp * I_mp);
        double I_0 = I_0_REF * Math.pow((Tc + 273.15) / (Tс_REF + 273.15), 3) * Math.exp(epsilon * Ns / a_REF * (1 - (Tс_REF + 273.15) / (Tc + 273.15)));
        double a = (Tc + 273.15) / (Tс_REF + 273.15) * a_REF;
        double ocV = biSectionalRoot(v -> I_L - I_0 * (Math.exp(v / a) - 1) - v / Rsh_REF, 0, ocVoltageRef * 5);
        builder.ocVoltage(ocV);
        Operation operation = builder.build();
        return operation;
    }

    private void initParams() {
        I_L_REF = scCurrentRef;
        Rsh_REF = Double.POSITIVE_INFINITY;
        Rs_REF = 0D;
        a_REF = (V_mp_REF - ocVoltageRef) / log(1 - I_mp_REF / scCurrentRef);
        I_0_REF = scCurrentRef * exp(-ocVoltageRef / a_REF);
    }

    private double calculateGb_eff(double Gb, SunPosition sunPosition, double betta) {
        double AOI = calculateAOI(sunPosition, betta); //угол падения лучей в радианах
//        double n = 1.534; // коэффициент оптического преломления стекла
//        double tet_r = asin(1 / n * sin(AOI)); //угол, с учётом искажения стекла
//        double K = 4; //константа пропорциональности
//        double L = 0.002; //толщина стекла
//        double tau = exp(-K * L / cos(tet_r)) * (1 - 1 / 2.0 * (pow(sin(tet_r - AOI) / sin(tet_r + AOI), 2) + pow(tan(tet_r - AOI) / tan(tet_r + AOI), 2)));
        AOI = AOI / PI * 180;
        double[] b = {
                1,
                -4.6445E-3,
                5.8607E-4,
                -2.3108E-5,
                3.7843E-7,
                -2.2515E-9};
        double tau = b[0] + b[1] * AOI + b[2] * pow(AOI, 2) + b[3] * pow(AOI, 3) + b[4] * pow(AOI, 4) + b[5] * pow(AOI, 5);
        if (tau > 1) tau = 1;
        return tau > 0 ? tau * cos(AOI * PI / 180) * Gb : 0;
    }

    private static double biSectionalRoot(DoubleUnaryOperator operator, double x1, double x2) {
        double f1 = operator.applyAsDouble(x1);
        double f2 = operator.applyAsDouble(x2);
        if (f1 * f2 > 0)
            throw new RuntimeException("Invalid value in bisectional method: f(x1)=" + x1 + " f(x2)=" + x2);
        double x = 0.5; // the root using the method of bisection.
        double f_of_c;
        final double TOLERANCE = 0.0001;
        while (Math.abs(x1 - x2) > TOLERANCE) {
            x = (x1 + x2) / 2;

            f_of_c = operator.applyAsDouble(x);
            if (f_of_c * operator.applyAsDouble(x1) == 0 || f_of_c * operator.applyAsDouble(x2) == 0) {
                x1 = x;
                x2 = x;
            } else if (f_of_c * operator.applyAsDouble(x1) > 0) {
                x1 = x;
            } else {
                x2 = x;
            }
        }
        return x;
    }

    /**
     * Метод расчитывает угол падения излучения (AOI) на поверхность ориентированной панели
     *
     * @return AOI, в радианах
     */
    private double calculateAOI(SunPosition sunPosition, double betta) {
        double a = sin(PI / 180.0 * (90 - sunPosition.getElevation())) * cos((sunPosition.getAzimuth() - 180) / 180.0 * PI) * sin(betta * PI / 180.0) + cos(PI / 180 * (90 - sunPosition.getElevation())) * cos(betta * PI / 180.0);
        double AOI;
        if (a >= -1 && a <= 1) AOI = acos(a);
        else if (a < -1) AOI = PI;
        else AOI = 0;
        return AOI;
    }

    /**
     * Метод вычисляет температуру модуля на основе температуры окружающего воздуха, скорости ветра и интенсивности
     * излучения. Для вычисления, должны быть обязательно инициализированы поля класса.
     *
     * @param Ta        температура в градусах цельсия
     * @param Geff      излучения в Вт/м2
     * @param windSpeed скорость ветра в м/с
     * @return температура градусах цельсия
     */
    private double calculateTc(double Ta, double Geff, double windSpeed) {
        double Gnoct = 800; // излучение при NOCT
        return Ta + (Tnoct - 20) * Geff / Gnoct * 9.5 / (5.7 + 3.8 * windSpeed);
    }

    @SuppressWarnings("Duplicates")
    private double calculateCurrent(double voltage, double Ta, double Geff, double windSpeed) {
        Ta -= 273.3;
//        G = G / sin(sunPosition.getElevation() * PI / 180);
//        if (sunPosition.getElevation() < 0 || G == 0)
//            return 0;
//        double Geff = calculateGb_eff(G, sunPosition);
        if (Geff <= 0) return 0;
        double Tc = calculateTc(Ta, Geff, windSpeed);
        double I_L = Geff / Geff_REF * (I_L_REF + alphaSC * (Tc - Tс_REF));
        //System.out.println("Geff: "+Geff+" G: "+G+" I_L: "+I_L+" Ta: "+Ta);
        double I_0 = I_0_REF * Math.pow((Tc + 273.15) / (Tс_REF + 273.15), 3) * Math.exp(epsilon * Ns / a_REF * (1 - (Tс_REF + 273.15) / (Tc + 273.15)));
        double a = (Tc + 273.15) / (Tс_REF + 273.15) * a_REF;
        double ocV = biSectionalRoot(v -> I_L - I_0 * (Math.exp(v / a) - 1) - v / Rsh_REF, 0, ocVoltageRef * 5);
        return newtonMethod(
                I -> -I + I_L - I_0 * (exp((voltage + I * Rs_REF) / a) - 1) - (voltage + I * Rs_REF) / Rsh_REF,
                I -> -1 - I_0 * exp((voltage + I * Rs_REF) / a) * Rs_REF / a - Rs_REF / Rsh_REF,
                6.64974577
        );
    }

    private void jacoby() {
        double[][] jacoby = new double[4][4]; //первый индекс - строка, второй - столбец
        double[] roots = {scCurrentRef, 1E-10, 0.5, 2};//Начальные приближения корней 0: I_l; 1: I_0; 2: Rs; 3: a; 4: Rsh
        double[] roots_i = Arrays.copyOf(roots, roots.length);
        double TOLERANCE = 0.001;
        int loopCount = 0;
        boolean condition = true;
        do {
            roots = Arrays.copyOf(roots_i, roots_i.length);
            //инициализируем матрицу Якоби
            jacoby = generateJacoby(roots);
            SimpleMatrix matrix = new SimpleMatrix(jacoby);
            double determinant = matrix.determinant();
            double absDet = abs(determinant);
            while (absDet < 1E-200) {
                for (int i = 0; i < 4; i++) {
                    roots_i[i] = roots[i] * 1.1;
                }
                jacoby = generateJacoby(roots_i);
                matrix = new SimpleMatrix(jacoby);
                determinant = matrix.determinant();
            }

            double[][] func = calculateVIfunction(roots);
            for (int i = 0; i < 4; i++) System.out.print(func[i][0] + " ");
            System.out.println();
            SimpleMatrix foo = new SimpleMatrix(func);
            matrix = matrix.invert().mult(foo);
            for (int i = 0; i < 4; i++) roots_i[i] = roots[i] - matrix.get(i, 0);
            condition = abs(roots_i[0] - roots[0]) > TOLERANCE
                    || abs(roots_i[1] - roots[1]) > TOLERANCE
                    || abs(roots_i[2] - roots[2]) > TOLERANCE
                    || abs(roots_i[3] - roots[3]) > TOLERANCE
                    || abs(roots_i[4] - roots[4]) > TOLERANCE;
        } while (condition);
        double x1 = roots_i[0] - roots[0];
        double x2 = roots_i[1] - roots[1];
        double x3 = roots_i[2] - roots[2];
        double x4 = roots_i[3] - roots[3];
        double x5 = roots_i[4] - roots[4];
        I_L_REF = roots_i[0];
        I_0_REF = roots_i[1];
        Rs_REF = roots_i[2];
        a_REF = roots_i[3];
        Rsh_REF = roots_i[4];
        //doubleMatrix.d
        // TODO: 10.03.2017 доделать
    }

    private double[][] generateJacoby(double[] roots) {
        double I_L = roots[0];
        double I_0 = roots[1];
        double Rs = roots[2];
        double a = roots[3];
        DoubleBinaryOperator der1 = (I, V) -> 1;
        DoubleBinaryOperator der2 = (I, V) -> -exp((I * Rs + V) / a) + 1;
        DoubleBinaryOperator der3 = (I, V) -> -I_0 * exp((I * Rs + V) / a) * I / a - I / Rsh_REF;
        DoubleBinaryOperator der4 = (I, V) -> I_0 * exp((I * Rs + V) / a) * (I * Rs + V) / a / a;
        double[][] jacoby = {
                {1, 0, -scCurrentRef / Rsh_REF, 0},
                {der1.applyAsDouble(I_mp_REF, V_mp_REF), der2.applyAsDouble(I_mp_REF, V_mp_REF), der3.applyAsDouble(I_mp_REF, V_mp_REF), der4.applyAsDouble(I_mp_REF, V_mp_REF)},
                {der1.applyAsDouble(0, ocVoltageRef), der2.applyAsDouble(0, ocVoltageRef), der3.applyAsDouble(0, ocVoltageRef), der4.applyAsDouble(0, ocVoltageRef)},
                new double[4]
        };
        jacoby[3][0] = 1;
        jacoby[3][1] = 1 - exp((V_mp_REF + I_mp_REF * Rs) / a) * (1 + V_mp_REF / a);
        jacoby[3][2] = -I_0 * I_mp_REF / a * (V_mp_REF / a + 1) * exp((V_mp_REF + I_mp_REF * Rs) / a) - I_mp_REF / Rsh_REF;
        jacoby[3][3] = exp(V_mp_REF + I_mp_REF * Rs) / a / a * ((1 + V_mp_REF / a) * (V_mp_REF + I_mp_REF * Rs) + 1);
        for (int i = 0; i < 4; i++) {
            System.out.print("[");
            for (int j = 0; j < 4; j++) {
                System.out.print(jacoby[i][j] + " ");
            }
            System.out.println("]");
        }
        System.out.println();
        return jacoby;
    }


    private double backCurrent(double I_0_REF, double Tc, double a_REF) {
        return I_0_REF * Math.pow((Tc + 273.15) / (Tс_REF + 273.15), 3) * Math.exp(epsilon * Ns / a_REF * (1 - (Tс_REF + 273.15) / (Tc + 273.15)));
    }

    private double idealityFactor(double Tc, double a_REF) {
        return (Tc + 273.15) / (Tс_REF + 273.15) * a_REF;
    }

    private double[][] calculateVIfunction(double[] roots) {
        double I_L = roots[0];
        double I_0 = roots[1];
        double Rs = roots[2];
        double a = roots[3];
        DoubleBinaryOperator operator = (I, V) -> I_L - I_0 * (exp((V + I * Rs) / a) - 1) + (V + I * Rs) / Rsh_REF;
        DoubleBinaryOperator operator2 =
                (I, V) -> (-I_0 * exp((V + I * Rs) / a) / a - 1 / Rsh_REF) * V + I_L - I_0 * (exp((V + I * Rs) / a) - 1) - (V + I * Rs) / Rsh_REF;
        return new double[][]{
                {operator.applyAsDouble(scCurrentRef, 0)},
                {operator.applyAsDouble(I_mp_REF, V_mp_REF)},
                {operator.applyAsDouble(0, ocVoltageRef)},
                {operator2.applyAsDouble(I_mp_REF, V_mp_REF)}
        };
    }

    /**
     * Метод для решения функции методом Ньютона
     *
     * @param operator   функция
     * @param derivative производная функции
     * @param root       начальное приближение
     * @return корень уравнния
     */
    private static double newtonMethod(DoubleUnaryOperator operator, DoubleUnaryOperator derivative, double root) {
        double xi = root;
        double xi1 = root;
        do {
            xi = xi1;
            xi1 = xi - operator.applyAsDouble(xi) / derivative.applyAsDouble(xi);
        } while (Math.abs(xi - xi1) > 0.001);
        return xi1;
    }

    public static void main(String[] args) {
        SolarModule Soltech_1STH_240_WH = SolarModule.builder()
                .Tс_REF(25D)
                .Ns(60)
                .scCurrentRef(8.58)
                .ocVoltageRef(37.1)
                .I_mp_REF(8.07)
                .V_mp_REF(29.7)
                .alphaSC(0.007465)
                .bettaOC(-0.1369)
                .Geff_REF(1000D)
                .area(1.635)
                .epsilon(1.12)
                .Gnoct(800D)
                .Tnoct(45D)
                .I_0_REF(1.325E-9)
                .I_L_REF(8.582)
                .Rs_REF(0.335)
                .a_REF(1.6425)
                .Rsh_REF(1463.82)
                .build();
        SunPosition[] sunPosition = {
                new SunPosition(90, 10),
                new SunPosition(150, 30),
                new SunPosition(180, 29),
                new SunPosition(210, 30)};
        Stream.of(sunPosition).map(sp -> Soltech_1STH_240_WH.calculateGb_eff(1000, sp, 60)).forEach(System.out::println);
//        Soltech_1STH_240_WH.printStandartOperation();
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 1, 2, 13, 0, 0, 0, ZoneId.of("UTC+3"));
//        SunPosition sunPosition = new SunPosition(new Position(40, 30, 20), zonedDateTime.toInstant());
//        Operation winter = Soltech_1STH_240_WH.getOperation(273.3, 179.12, 0, 2.24, sunPosition, 17);
//        System.out.println(winter);
//        Soltech_1STH_240_WH.printOperation(winter);
//        System.out.println(Soltech_1STH_240_WH.calculateAOI(new SunPosition(180, 60), 30) / PI * 180);
//        System.out.println(Soltech_1STH_240_WH.calculateMMP(winter));
    }

    private void printOperation(Operation operation) {
        Map<Double, Double> map = new HashMap<>();
        for (double v = 0; v <= operation.ocVoltage; v = v + 0.02) {
            //System.out.println(v + " " + Soltech_1STH_240_WH.calculateCurrent(v, -6 + 273, 850.7, 1, sunPosition));
            map.put(v, calculateCurrent(v, operation.Ta + 273.3, operation.Geff, operation.windSpeed));
        }

        LineChartNumber.setChartTitle("Вольтамперная характеристика");
        LineChartNumber.setSeriesName(" ");
        LineChartNumber.setXAxisLabel("Voltage");
        LineChartNumber.goWith(map);
    }
}