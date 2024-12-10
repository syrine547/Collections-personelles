package Entity;

public class PieceMonnaie {
    private int idPiecesMonnaie;
    private String valeurPiecesMonnaie;
    private String unitéPiecesMonnaie;

    public PieceMonnaie(int idPiecesMonnaie, String valeurPiecesMonnaie, String unitéPiecesMonnaie) {
        this.idPiecesMonnaie = idPiecesMonnaie;
        this.valeurPiecesMonnaie = valeurPiecesMonnaie;
        this.unitéPiecesMonnaie = unitéPiecesMonnaie;
    }

    public int getIdPiecesMonnaie() {
        return idPiecesMonnaie;
    }
    public void setIdPiecesMonnaie(int id) {
        this.idPiecesMonnaie = id;
    }

    public String getValeurPiecesMonnaie() {
        return valeurPiecesMonnaie;
    }
    public void setValeurPiecesMonnaie(String valeur) {this.valeurPiecesMonnaie = valeur;}

    public String getUnitéPiecesMonnaie() {
        return unitéPiecesMonnaie;
    }
    public void setUnitéPiecesMonnaie(String unité) {
        this.unitéPiecesMonnaie = unité;
    }

    @Override
    public String toString() {
        return "Piece monnaie [ID= " + idPiecesMonnaie + ", valeur= " + valeurPiecesMonnaie + ", unité= " + unitéPiecesMonnaie+"]";
    }

}
