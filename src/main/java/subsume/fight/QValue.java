package subsume.fight;

import subsume.Decision;

//TODO - do I need that?
public class QValue {

    private final CondensedState state;
    private final Decision decision;

    public QValue(CondensedState state, Decision decision) {
        this.state = state;
        this.decision = decision;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QValue qValue = (QValue) o;

        if (decision != null ? !decision.equals(qValue.decision) : qValue.decision != null) return false;
        if (state != null ? !state.equals(qValue.state) : qValue.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (decision != null ? decision.hashCode() : 0);
        return result;
    }
}