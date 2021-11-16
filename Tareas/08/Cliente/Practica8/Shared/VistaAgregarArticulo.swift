//
//  VistaAgregarArticulo.swift
//  Practica8
//
//  Created by René Payán Téllez on 15/11/21.
//

import SwiftUI

struct VistaAgregarArticulo: View {
    @State private var input_descripcion: String = ""
    @State private var input_precio: String = ""
    @State private var input_inventario: String = ""
    @State private var image: Image?
    @State private var mostrandoPicker: Bool = false
    @State private var inputImage: UIImage?
    @State private var hayError: Bool = false
    @State private var operacionExitosa: Bool = false
    @State private var regresarAlMenu: Bool = false
    @State private var textoError:String = ""
    @Binding var cualVentana: Int32
    
    var body: some View {
        NavigationView{
            Form{
                VStack{
                    Group{
                        Group{
                            Text("Descripcion:")
                            TextEditor(text: $input_descripcion)
                                .border(Color.blue)
                                .frame( minHeight: 150, alignment: Alignment.center)
                        }
                        Group{
                            Text("Inventario:")
                            TextField("Inventario", text: $input_inventario)
                        }
                        Group{
                            Text("Precio:")
                            TextField("Precio", text: $input_precio)
                        }
                        Group{
                            Text("Foto: ")
                            ZStack{
                                Rectangle().fill(Color.secondary)
                                    if image != nil{
                                        image?.resizable().scaledToFit()
                                    }else{
                                        Text("Presione para seleccionar la imagen").foregroundColor(.white).font(.headline)
                                    }
                            }.onTapGesture{
                                self.mostrandoPicker = true
                            }.padding(.bottom)
                        }
                    }
                }
                HStack{
                    Button(action: registrarArticulo){
                        Text("Registrar")
                    }.alert(isPresented: $hayError){
                        Alert(
                            title:
                                Text( "Error al registrar el articulo").foregroundColor(.red)
                            ,message:
                                Text(textoError)
                            ,dismissButton:
                                    .default(
                                        Text("Aceptar")
                                    )
                        )
                    }.alert(isPresented: $operacionExitosa){
                        Alert(
                            title:
                                Text( "Exito al registrar el articulo").foregroundColor(.green)
                            ,message:
                                Text("Articulo registrado exitosamente")
                            ,dismissButton:
                                    .default(
                                        Text("Aceptar")
                                    )
                        )
                    }
                }
                Button(action: {
                    self.cualVentana = 0;
                }){
                    Text("Regresar")
                }
            }.navigationBarTitle("Agregar articulo")
                .sheet(isPresented: $mostrandoPicker, onDismiss: loadImage){
                    ImagePicker(image: self.$inputImage)
            }
        }
    }
    func regresar(){
        regresarAlMenu = true
        //ContentView()
    }
    func registrarArticulo() -> Void{
        hayError = false;
        operacionExitosa = false;
        let descripcion:String = input_descripcion
        
        let precio:Double! = Double(input_precio)
        
        let inventario:Int64! = Int64(input_inventario)
        
        let dataPng: Data = inputImage!.pngData()!
        let fotoData:[UInt8] = [UInt8](dataPng)
        
        let foto:Foto = Foto(idFoto: 0, imagen: fotoData, tipo: "image/png")
        
        let nuevoArticulo = Articulo(idArticulo: 0, descripcion: descripcion, precio: precio, inventario: inventario, foto: foto)
        print(nuevoArticulo.toJSON())
        //textoError += "Valio madres";
    }
    func loadImage(){
        guard let inputImage = inputImage else {
            return
        }
        image = Image(uiImage: inputImage)
    }
}

struct VistaAgregarArticulo_Previews: PreviewProvider {
    static var previews: some View {
        VistaAgregarArticulo(cualVentana: .constant(1))
    }
}
