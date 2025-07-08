package Modelos.Entidades;

public class Butaca {
    private int id;
    private int salaId;
    private int fila;
    private int columna;
    private EstadoButaca estado;

    public Butaca(int id, int salaId, int fila, int columna, EstadoButaca estado) {
        this.id = id;
        this.salaId = salaId;
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSalaId() { return salaId; }
    public void setSalaId(int salaId) { this.salaId = salaId; }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public EstadoButaca getEstado() { return estado; }
    public void setEstado(EstadoButaca estado) { this.estado = estado; }
}