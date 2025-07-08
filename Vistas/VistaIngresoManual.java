package Vistas;

import Modelos.Entidades.*;
import Modelos.Persistencia.*;
import java.util.Scanner;
import java.util.List;

public class VistaIngresoManual {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SalasDAO salaDAO = new SalasDAO();
        ButacasDAO butacasDAO = new ButacasDAO();

        try {
            // Ingreso manual de sala
            System.out.print("Nombre de la sala: ");
            String nombre = scanner.nextLine();
            System.out.print("Capacidad de la sala: ");
            int capacidad = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            System.out.print("Tipo de sala (NORMAL, VIP): ");
            TipoSala tipo = TipoSala.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Estado de sala (DISPONIBLE, MANTENIMIENTO): ");
            EstadoSala estado = EstadoSala.valueOf(scanner.nextLine().toUpperCase());

            Sala sala = new Sala(0, nombre, capacidad, tipo, estado);
            salaDAO.crearSala(sala);

            // Obtener el id de la sala recién creada
            List<Sala> salas = salaDAO.listarSalas();
            int salaId = salas.get(salas.size() - 1).getId();

            // Ingreso manual de butacas
            for (int i = 1; i <= capacidad; i++) {
                System.out.println("Butaca #" + i);
                System.out.print("Fila: ");
                int fila = scanner.nextInt();
                System.out.print("Columna: ");
                int columna = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer
                System.out.print("Estado de butaca (INHABILITADA, DISPONIBLE, OCUPADA, RESERVADA): ");
                EstadoButaca estadoButaca = EstadoButaca.valueOf(scanner.nextLine().toUpperCase());

                Butaca butaca = new Butaca(0, salaId, fila, columna, estadoButaca);
                try {
                    butacasDAO.crearButaca(butaca);
                    System.out.println("Butaca creada.");
                } catch (Exception e) {
                    System.out.println("Error al crear butaca: " + e.getMessage());
                }
            }

            System.out.println("Ingreso manual finalizado.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}