package Project1;

/**
 * Этот класс демонстрирует сортировку массива с помощью алгоритма пузырьковой сортировки.
 * @author Михаил
 * @version 1.0
 */
public class Program {
    /**
     * Статический массив целых чисел для сортировки.
     * Инициализируется набором произвольных значений в методе main.
     */
    static int[] numbers;

    /**
     * Главный метод, который выводит приветствие,инициализирует массив, выводит его,  сортирует его и выводит отсортированный.
     * Использует алгоритм пузырьковой сортировки для сортировки массива в порядке возрастания.
     * Алгоритм включает многократные проходы по массиву и сравнения соседних элементов.
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        System.out.println("Hello Eclipse!");
        
        numbers = new int[]{5, 2, 8, 1, 9, 3, 7, 4, 6};
        
        
        System.out.println("Исходный массив:");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
    
        System.out.println(" \n "); 
        
        for (int i = 0; i < numbers.length - 1; i++) {
            for (int j = 0; j < numbers.length - i - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
        
        System.out.println("По возрастанию:");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}