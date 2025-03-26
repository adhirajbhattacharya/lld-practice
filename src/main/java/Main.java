public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        System.out.println(main.coinChange(new int[] {1, 2, 5}, 11));
    }

    static int MAX = 100000;
    public int coinChange(int[] coins, int amount) {
        int res = minCoins(coins, amount);
        return res >= MAX ? -1 : res;
    }

    private int minCoins(int[] coins, int amount) {
        if (amount < 0) return MAX;
        if (amount == 0) return 0;

        int res = MAX;

        for (int coin : coins) {
            int rem = amount - coin;
            int remcomb = minCoins(coins, rem);
            if (remcomb != MAX) {
                res = Math.min(remcomb + 1, rem);
            }
        }

        return res;
    }
}
