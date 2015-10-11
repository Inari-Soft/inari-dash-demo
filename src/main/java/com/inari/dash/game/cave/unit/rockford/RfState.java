package com.inari.dash.game.cave.unit.rockford;

public enum RfState {
    APPEARING,          // Rockford appears in a short explosion where the blinking door was before
    IDLE,               // Rockford idle state, no move, no animation
    IDLE_BLINKING,      // Rockford eyes are blinking
    IDLE_FRETFUL,       // Rockford is fretful waiting for user interaction
    MOVE_UP,
    MOVE_RIGHT,
    MOVE_DOWN,
    MOVE_LEFT,
    GRAP_UP,
    GRAP_RIGHT,
    GRAP_DOWN,
    GRAP_LEFT,
    DIED,
    WON
}
