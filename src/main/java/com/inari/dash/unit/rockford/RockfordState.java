package com.inari.dash.unit.rockford;

public enum RockfordState {
    APPEARING,          // Rockford appears in a short explosion where the binking door was before
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
    EXPLODING,
    WON
}
