package harp.util;

public class Container implements ContainerProps {

    public Container() {
        //System.out.println("Container Contruct");
        //super.sequenceNumber = StringUtil.extractCountable(this.getCode());
    }

    Container[] container = new Container[0];

    protected int sequenceNumber;

    public Container[] getContainer() {
        return container;
    }

    public void setContainer(Container[] container) {
        this.container = container;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public <E> void add(E[] target, E item) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public void addAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAll'");
    }

    @Override
    public Object getFrom(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFrom'");
    }

    @Override
    public void setAt(int index, Object element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAt'");
    }

    @Override
    public void insert(int from, int to) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void remove(int from, int to) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public void replace(int from, Object element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replace'");
    }

    @Override
    public void replace(Object prevElement, Object newElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replace'");
    }

}
