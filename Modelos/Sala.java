package Modelos;

public class Sala {
    private int id;
    private int numeroFilas;
    private int numeroColumnas;
    private TipoSala tipoSala;
    private EstadoSala estadoSala;
    private Butaca[][] mapaDeButacas;

    public Sala(int id, int numeroFilas, int numeroColumnas, TipoSala tipoSala, EstadoSala estadoSala) {
        this.id = id;

        this.numeroFilas = numeroFilas;
        this.numeroColumnas = numeroColumnas;
        this.tipoSala = tipoSala;
        this.estadoSala = estadoSala;
        this.mapaDeButacas = new Butaca[numeroFilas][numeroColumnas];
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public Butaca[][] getMapaDeButacas() { return mapaDeButacas; }
    public void setMapaDeButacas(Butaca[][] mapaDeButacas) { this.mapaDeButacas = mapaDeButacas; }
    public int getNumeroFilas() { return numeroFilas; }
    public void setNumeroFilas(int numeroFilas) { this.numeroFilas = numeroFilas; }
    public int getNumeroColumnas() { return numeroColumnas; }
    public void setNumeroColumnas(int numeroColumnas) { this.numeroColumnas = numeroColumnas; }
    public TipoSala getTipoSala() { return tipoSala; }
    public void setTipoSala(TipoSala tipoSala) { this.tipoSala = tipoSala; }

    public EstadoSala getEstadoSala() { return estadoSala; }
    public void setEstadoSala(EstadoSala estadoSala) { this.estadoSala = estadoSala; }

    public EstadoSala getEstado() {
        return estadoSala;
    }
    public EstadoSala setEstado(EstadoSala estado) {
        this.estadoSala = estado;
        return this.estadoSala;
    }
}
