/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.CroppedImage;
import sv.gob.mined.app.web.util.VarSession;

/**
 *
 * @author misanchez
 */
@ManagedBean
@ViewScoped
public class ImagenController implements Serializable{

    private Boolean exibeImagemCapturada;
    private String foto;
    private String fotoRecortada;
    private String archivoFoto;
    private String archivoFotoRecortada;
    private CroppedImage imagemRecortada;
    private ServletContext servletContext;

    /**
     * Creates a new instance of ImagenController
     */
    public ImagenController() {
        exibeImagemCapturada = false;
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    @PostConstruct
    public void init() {
        if (VarSession.isVariableSession("nitEmpresa")) {
            archivoFoto = VarSession.getVariableSession("nitEmpresa").toString().concat("").concat(".png");
            VarSession.removeVariableSession("idEmpresa");
        }
    }

    public CroppedImage getImagemRecortada() {
        return imagemRecortada;
    }

    public void setImagemRecortada(CroppedImage imagemRecortada) {
        this.imagemRecortada = imagemRecortada;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotoRecortada() {
        return fotoRecortada;
    }

    public String getFotoR() {
        return FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + "resources" + File.separator + "images" + File.separator + "temp" + File.separator + fotoRecortada;
    }

    public void setFotoRecortada(String fotoRecortada) {
        this.fotoRecortada = fotoRecortada;
    }

    public boolean isExibeImagemCapturada() {
        return exibeImagemCapturada;
    }

    public void setExibeImagemCapturada(boolean exibeImagemCapturada) {
        this.exibeImagemCapturada = exibeImagemCapturada;
    }

    public void recortar() {
        verificaExistenciaArquivo(VarSession.getVariableSession("nitEmpresa").toString().concat("").concat(".png"));
        fotoRecortada = "fotoRecortada" + getRandomImageName() + ".png";
        archivoFotoRecortada = File.separator + "imagenes" + File.separator + "PaqueteEscolar" + File.separator + "fotoProveedores" + File.separator + VarSession.getVariableSession("nitEmpresa").toString().concat("").concat(".png");
        crearArchivo(servletContext.getRealPath(File.separator + "resources" + File.separator + "images" + File.separator + "temp" + File.separator + fotoRecortada), imagemRecortada.getBytes());
    }

    public void oncapture(CaptureEvent captureEvent) {
        verificaExistenciaArquivo(archivoFoto);
        foto = "foto" + getRandomImageName() + ".png";
        archivoFoto = servletContext.getRealPath(File.separator + "resources" + File.separator + "images" + File.separator + "temp" + File.separator + foto);
        crearArchivo(archivoFoto, captureEvent.getData());
        exibeImagemCapturada = true;
    }
    
    public void oncaptureFotoZapato(CaptureEvent captureEvent) {
        verificaExistenciaArquivo(archivoFoto);
        foto = "foto" + getRandomImageName() + ".png";
        archivoFoto = servletContext.getRealPath(File.separator + "resources" + File.separator + "images" + File.separator + "temp" + File.separator + foto);
        crearArchivo(archivoFoto, captureEvent.getData());
        exibeImagemCapturada = true;
    }
    

    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
        return String.valueOf(i);
    }

    private void verificaExistenciaArquivo(String arquivo) {
        if (arquivo != null) {
            File file = new File(arquivo);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void crearArchivo(String archivo, byte[] dados) {
        FileImageOutputStream imageOutput;
        try {
            File f = new File(archivo);
            f.setReadable(true);
            f.setExecutable(true);
            f.setWritable(true);
            imageOutput = new FileImageOutputStream(f);
            imageOutput.write(dados, 0, dados.length);
            imageOutput.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImagenController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Caminho não encontrado!", "Erro"));
        } catch (IOException ex) {
            Logger.getLogger(ImagenController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao criar arquivo!", "Erro"));
        }
    }

    public void cerrarDlgFoto() {
        crearArchivo(archivoFotoRecortada, imagemRecortada.getBytes());
        PrimeFaces.current().dialog().closeDynamic(fotoRecortada);
    }

    public void cerrarDlgFotoZapato() {
        recortar();
        File carpetaNfs = new File(VarSession.getVariableSession("carpetaFoto").toString());
        File temp = new File(VarSession.getVariableSession("carpetaFoto").toString() + File.separator+ String.valueOf(carpetaNfs.list().length)+".jpg");
        crearArchivo(temp.getAbsolutePath(), imagemRecortada.getBytes());
        PrimeFaces.current().dialog().closeDynamic(fotoRecortada);
    }

    public void cancelFoto() {
        PrimeFaces.current().dialog().closeDynamic("/");
    }
}