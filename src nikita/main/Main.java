package main;

import logic.LaundryQueue;
import models.BulkyLaundry;
import models.LaundryRequest;
import models.RegularLaundry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static LaundryQueue<LaundryRequest> queue;
    private static final int N = 16;
    private static int variantA;
    private static int variantB;
    private static int variantC;
    private static int completedStage = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        variantA = StageAccess.initial(N);
        System.out.println("Назначен вариант задания 1: А" + variantA);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1. Выполнить задание 1 и вывести назначенный вариант задания 2");
            System.out.println("2. Выполнить задание 2 и вывести назначенный вариант задания 3");
            System.out.println("3. Выполнить задание 3 и вывести отчёт");
            System.out.println("4. Показать очередь, статусы и вместимость машины");
            System.out.println("5. Выход из программы");
            System.out.print("Выберите пункт: ");
            String line = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число от 1 до 5.");
                continue;
            }
            switch (choice) {
                case 1:
                    stageOne();
                    break;
                case 2:
                    stageTwo();
                    break;
                case 3:
                    stageThree();
                    break;
                case 4:
                    showQueue();
                    break;
                case 5:
                    running = false;
                    System.out.println("Выход из программы.");
                    break;
                default:
                    System.out.println("Ошибка: введите число от 1 до 5.");
            }
        }
        scanner.close();
    }

    private static List<LaundryRequest> createRequests() {
        List<LaundryRequest> list = new ArrayList<>();
        list.add(new RegularLaundry(301, 31, 3, 2, 5));
        list.add(new RegularLaundry(303, 32, 4, 2, 5));
        list.add(new RegularLaundry(305, 33, 2, 2, 2));
        list.add(new RegularLaundry(308, 34, 6, 2, 3));
        list.add(new RegularLaundry(310, 35, 11, 1, 5));
        list.add(new BulkyLaundry(302, 31, 4, 1, 4, 1));
        list.add(new BulkyLaundry(304, 32, 2, 1, 3, 2));
        list.add(new BulkyLaundry(306, 33, 5, 1, 4, 2));
        list.add(new BulkyLaundry(307, 34, 1, 1, 1, 1));
        list.add(new BulkyLaundry(309, 32, 1, 1, 4, 1));
        return list.stream()
                .sorted(Comparator.comparingInt(LaundryRequest::getId))
                .collect(Collectors.toList());
    }

    private static void stageOne() {
        if (completedStage >= 1) {
            System.out.println("Этап 1 уже выполнен.");
            return;
        }
        Predicate<LaundryRequest> selection = r -> r.getLoadUnits() <= 4 && r.getPriority() >= 2;
        LaundryQueue<LaundryRequest> newQueue = new LaundryQueue<>(10);
        for (LaundryRequest request : createRequests()) {
            if (selection.test(request)) {
                newQueue.add(request);
            }
        }
        List<Integer> ids = newQueue.getAnalyticsStream()
                .map(LaundryRequest::getId)
                .sorted()
                .collect(Collectors.toList());
        int load = newQueue.getAnalyticsStream().mapToInt(LaundryRequest::getLoadUnits).sum();
        System.out.println("ID добавленных заявок: " + ids);
        System.out.println("Сумма загрузок: " + load);
        try {
            variantB = StageAccess.second(N, ids, load);
            queue = newQueue;
            completedStage = 1;
            System.out.println("Назначен вариант задания 2: Б" + variantB);
        } catch (IllegalStateException e) {
            System.out.println("Ошибка проверки: " + e.getMessage() + ". Проверьте решение.");
        }
    }

    private static void stageTwo() {
        if (completedStage < 1) {
            System.out.println("Сначала выполните этап 1.");
            return;
        }
        if (completedStage >= 2) {
            System.out.println("Этап 2 уже выполнен.");
            return;
        }
        List<LaundryRequest> accepted = runBatch();
        List<Integer> ids = accepted.stream()
                .map(LaundryRequest::getId)
                .collect(Collectors.toList());
        int load = accepted.stream().mapToInt(LaundryRequest::getLoadUnits).sum();
        int mode = accepted.isEmpty() ? 0 : accepted.get(0).getMode();
        System.out.println("ID принятых заявок: " + ids);
        System.out.println("Загрузка цикла: " + load);
        System.out.println("Режим цикла: " + mode);
        try {
            variantC = StageAccess.third(N, ids, load, mode);
            completedStage = 2;
            System.out.println("Назначен вариант задания 3: В" + variantC);
        } catch (IllegalStateException e) {
            System.out.println("Ошибка проверки: " + e.getMessage() + ". Проверьте решение.");
        }
    }

    private static void stageThree() {
        if (completedStage < 2) {
            System.out.println("Сначала выполните этап 2.");
            return;
        }
        if (completedStage >= 3) {
            System.out.println("Этап 3 уже выполнен.");
            return;
        }
        if (!queue.setCapacity(12)) {
            System.out.println("Ошибка: не удалось изменить вместимость.");
            return;
        }
        List<LaundryRequest> accepted = runBatch();
        List<Integer> ids = accepted.stream()
                .map(LaundryRequest::getId)
                .collect(Collectors.toList());
        int load = accepted.stream().mapToInt(LaundryRequest::getLoadUnits).sum();
        int mode = accepted.isEmpty() ? 0 : accepted.get(0).getMode();
        System.out.println("ID новых выстиранных заявок: " + ids);
        System.out.println("Загрузка цикла: " + load);
        System.out.println("Режим цикла: " + mode);
        Optional<LaundryRequest> report = queue.getAnalyticsStream()
                .filter(r -> !r.isWashed())
                .filter(r -> r.getLoadUnits() <= queue.getCapacity())
                .sorted(Comparator.comparingInt(LaundryRequest::getPriority).reversed()
                        .thenComparingInt(LaundryRequest::getLoadUnits)
                        .thenComparingInt(LaundryRequest::getId))
                .findFirst();
        System.out.println("Выбранная ожидающая заявка: "
                + report.map(LaundryRequest::getDetails).orElse("нет"));
        completedStage = 3;
    }

    private static List<LaundryRequest> runBatch() {
        Predicate<LaundryRequest> condition = r -> true;
        Comparator<LaundryRequest> order = Comparator.comparingInt(LaundryRequest::getPriority).reversed()
                .thenComparingInt(LaundryRequest::getLoadUnits)
                .thenComparingInt(LaundryRequest::getId);
        Consumer<LaundryRequest> action = LaundryRequest::markWashed;
        return queue.washBatch(condition, order, action);
    }

    private static void showQueue() {
        if (queue == null) {
            System.out.println("Очередь ещё не сформирована.");
            return;
        }
        queue.getAnalyticsStream().forEach(r -> System.out.println(r.getDetails()));
        System.out.println("Вместимость машины: " + queue.getCapacity());
    }
}

final class StageAccess {
    private static final int[] S = {2, 5, 4, 0, 1, 3};
    private static final int[] A = {
        0xec9a9bee, 0xe4008178, 0x90362d55, 0xfd76dc38, 0x26474398,
        0x09b07677
    };
    private static final int[] B = {
        0x3d997b45, 0x304e1445, 0xe673e62a, 0x3d997b45, 0x3d997b45,
        0xe30a38ad, 0x3d997b45, 0xb2d7f90f, 0x3d997b45, 0x3d997b45,
        0x3d997b45, 0xac048f11, 0xf9259ef1, 0xe673e62a, 0x5fcb48b5,
        0x5fcb48b5, 0xe673e62a, 0x5fcb48b5, 0xbe382650, 0x3d997b45,
        0x3d997b45, 0xdadf3629, 0x3d997b45, 0x304e1445, 0xe673e62a,
        0x5fcb48b5, 0x5fcb48b5, 0xe30a38ad, 0x5fcb48b5, 0x304e1445,
        0x41032501, 0x41032501, 0xe30a38ad, 0x41032501, 0xd4330e26,
        0xe673e62a
    };
    static int initial(int n) {
        if (n < 1 || n > 36)
            throw new IllegalArgumentException("N: 1..36");
        return (n - 1) / 6 + 1;
    }
    static int second(int n, java.util.List<Integer> ids, int load) {
        return check(n, ids, load, 0, A[initial(n) - 1], 0);
    }
    static int third(int n, java.util.List<Integer> ids,
                     int load, int mode) {
        int a = initial(n);
        int b = 1 + Math.floorMod((long) A[a - 1] + (n - 1) % 6, 6);
        return check(n, ids, load, mode, B[n - 1], b);
    }
    private static int check(int n, java.util.List<Integer> ids,
            int load, int mode, int expected, int previous) {
        if (ids == null) throw new IllegalArgumentException("ids: null");
        int actual = (ids + "|" + load + "|" + mode).hashCode();
        if (actual != expected)
            throw new IllegalStateException("Check stage result");
        int p = (n - 1) % 6;
        long key = previous == 0 ? (long) actual + p
            : ((actual >>> 31) ^ (actual >>> 2)) + 1L * p 
              + 0L * previous + S[initial(n) - 1];
        return 1 + Math.floorMod(key, 6);
    }
}
