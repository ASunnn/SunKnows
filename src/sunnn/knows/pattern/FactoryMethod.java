package sunnn.knows.pattern;

public class FactoryMethod {

    public static void main(String[] args) {
        OperationFactory factory = new DivFactory();

        Operation operation = factory.getOperation();

        System.out.println(operation.getResult(1, 0));
    }
}

/* ____________________ */

/* 产品类在SimpleFactory */

/* ____________________ */

interface OperationFactory {

    Operation getOperation();
}

class AddFactory implements OperationFactory {

    @Override
    public Operation getOperation() {
        return new Add();
    }
}

class SubFactory implements OperationFactory {

    @Override
    public Operation getOperation() {
        return new Sub();
    }
}

class MulFactory implements OperationFactory {

    @Override
    public Operation getOperation() {
        return new Mul();
    }
}

class DivFactory implements OperationFactory {

    @Override
    public Operation getOperation() {
        return new Div();
    }
}
