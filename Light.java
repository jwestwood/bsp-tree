
public class Light {

    private Vec mAmbient;
    private Vec mDiffuse;
    private Vec mSpeculative;

    public Light(Vec ambient, Vec diffuse, Vec speculative) {
        mAmbient = ambient;
        mDiffuse = diffuse;
        mSpeculative = speculative;
    }

    public void setAmbient(Vec ambient) {
        mAmbient = ambient;
    }

    public void setDiffuse(Vec diffuse) {
        mDiffuse = diffuse;
    }

    public void setSpeculative(Vec speculative) {
        mSpeculative = speculative;
    }

    public Vec ambient() {
        return mAmbient;
    }

    public Vec diff() {
        return mDiffuse;
    }

    public Vec spec() {
        return mSpeculative;
    }
}
