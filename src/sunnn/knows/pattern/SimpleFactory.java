package sunnn.knows.pattern;

public class SimpleFactory {

    public static void main(String[] args) {
        System.out.println(operationFactory("div").getResult(1, 0));
    }

    static Operation operationFactory(String o) {
        switch (o) {
            case "add":
                return new Add();
            case "sub":
                return new Sub();
            case "mul":
                return new Mul();
            case "div":
                return new Sub();
        }

        return null;
    }
}

interface Operation {

    int getResult(int a, int b);

}

class Add implements Operation {

    @Override
    public int getResult(int a, int b) {
        return a + b;
    }
}

class Sub implements Operation {

    @Override
    public int getResult(int a, int b) {
        return a - b;
    }
}

class Mul implements Operation {

    @Override
    public int getResult(int a, int b) {
        return a * b;
    }
}

class Div implements Operation {

    @Override
    public int getResult(int a, int b) {
        return a / b;
    }
}
