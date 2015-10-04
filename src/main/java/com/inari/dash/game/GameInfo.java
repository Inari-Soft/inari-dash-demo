package com.inari.dash.game;

import com.badlogic.gdx.files.FileHandle;

public class GameInfo {
    public enum CodeType {
        INTERN,
        BDCFF
    }
    
    public enum Gametype {
        BD // Original Boulder Dash
    }
    
    public enum Fontset { 
        Original
    }
    
    public enum Graphicset {
        Original
    }
    
    private FileHandle gameConfigResource;
    private CodeType type = CodeType.INTERN;
    private Gametype gameType = Gametype.BD;
    private Fontset fontset = Fontset.Original;
    private Graphicset graphicset = Graphicset.Original;
    private String name = "";
    private String description = "";
    private String date = "";
    private String author = "";
    private String www = "";
    private int caves;
    
    public Gametype getGameType() {
        return gameType;
    }

    public GameInfo setGameType( Gametype gameType ) {
        this.gameType = gameType;
        return this;
    }

    public Fontset getFontset() {
        return fontset;
    }

    public GameInfo setFontset( Fontset fontset ) {
        this.fontset = fontset;
        return this;
    }

    public Graphicset getGraphicset() {
        return graphicset;
    }

    public void setGraphicset( Graphicset graphicset ) {
        this.graphicset = graphicset;
    }

    public String getName() {
        return name;
    }

    public GameInfo setName( String name ) {
        this.name = name;
        return this;
    }

    public String getDate() {
        return date;
    }

    public GameInfo setDate( String date ) {
        this.date = date;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public GameInfo setAuthor( String author ) {
        this.author = author;
        return this;
    }

    public String getWww() {
        return www;
    }

    public GameInfo setWww( String www ) {
        this.www = www;
        return this;
    }
    
    public CodeType getType() {
        return type;
    }

    public GameInfo setType( CodeType type ) {
        this.type = type;
        return this;
    }
    
    public String getDescription() {
        return description;
    }

    public GameInfo setDescription( String description ) {
        this.description = description;
        return this;
    }

    public final FileHandle getGameConfigResource() {
        return gameConfigResource;
    }

    public final void setGameConfigResource( FileHandle gameConfigResource ) {
        this.gameConfigResource = gameConfigResource;
    }

    public int getCaves() {
        return caves;
    }

    public void setCaves( int caves ) {
        this.caves = caves;
    }


}
