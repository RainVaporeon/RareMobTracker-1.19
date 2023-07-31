package com.spiritlight.fishutils.action;

/**
 * Utility enum to indicate the result of an action.
 * If more information is required, use {@link ActionResult} instead.
 *
 * @see ActionResult
 */
public enum Result {
    /**
     * Indicating that this action is successful
     */
    SUCCESS,
    /**
     * Indicating that this action is unsuccessful due to insufficient permissions
     */
    NO_PERMISSION,
    /**
     * Indicating that this action is unsuccessful due to some sort of exception
     * during execution.
     * This sort of result usually provides further information to provide
     * insight on the cause.
     */
    ERROR,
    /**
     * Indicating that this action is unsuccessful due to some conditions not matching,
     * failed predicate or any unknown errors occurred.
     * This sort of result usually provides further information to provide
     * insight on the cause.
     */
    FAIL
}
