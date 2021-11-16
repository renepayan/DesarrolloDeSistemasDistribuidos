//
//  ContentView.swift
//  Shared
//
//  Created by René Payán Téllez on 15/11/21.
//

import SwiftUI

struct ContentView: View {
    @Binding var cualVentana:Int32
    var body: some View {
        VStack{
            Button(action:{
                self.cualVentana = 1
                print("ya le cambie")
            }){
                Text("Registrar articulo")
            }
            Button(action:{
                self.cualVentana = 2
            }){
                Text("Ver articulos")
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(cualVentana:.constant(0))
    }
}
//.sheet(isPresented: $irAgregarArticulo){
//VistaAgregarArticulo()
//}
