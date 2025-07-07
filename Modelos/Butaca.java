// ====== modelo/Butaca.java ======
package Modelos;

public class Butaca {
    private int id;
    private int fila;
    private int columna;
    private EstadoButaca estado;
    private int idSala;

    public Butaca() {}

    public Butaca(int id, int fila, int columna, EstadoButaca estado, int idSala) {
        this.id = id;
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
        this.idSala = idSala;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public EstadoButaca getEstado() { return estado; }
    public void setEstado(EstadoButaca estado) { this.estado = estado; }

    public int getIdSala() { return idSala; }
    public void setIdSala(int idSala) { this.idSala = idSala; }

}