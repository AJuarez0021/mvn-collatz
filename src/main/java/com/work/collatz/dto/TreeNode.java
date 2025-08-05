package com.work.collatz.dto;

/**
 *
 * @author ajuar
 */
public record TreeNode(long value,
        int depth,
        TreeNode parent,
        String operationType) {

    @Override
    public String toString() {
        return String.format("TreeNode{value=%d, depth=%d, op=%s}",
                value, depth, operationType);
    }
}
