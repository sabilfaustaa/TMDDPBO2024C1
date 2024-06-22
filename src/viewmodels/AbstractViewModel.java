package viewmodels;

public abstract class AbstractViewModel implements ViewModel {
    protected boolean isInitialized;

    @Override
    public void initialize() {
        isInitialized = true;
    }

    @Override
    public abstract void update();
}
