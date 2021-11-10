public class Respuesta{
    private int codigo = 500;
    private String contenido = "";
    public Respuesta(int codigo, String contenido){
        this.codigo = codigo;
        this.contenido = contenido;
    }
    public String getContenido(){
        return this.contenido;
    }
    public int getCodigo(){
        return this.codigo;
    }
}