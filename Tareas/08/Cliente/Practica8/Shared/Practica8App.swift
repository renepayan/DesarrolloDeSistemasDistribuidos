//
//  Practica8App.swift
//  Shared
//
//  Created by René Payán Téllez on 15/11/21.
//

import SwiftUI

@main
struct Practica8App: App {
    @State private var cualVentana: Int32 = 0
    var body: some Scene {
        WindowGroup {
            switch(cualVentana){
            case 0:
                ContentView(cualVentana: self.$cualVentana)
                //break
            case 1:
                VistaAgregarArticulo(cualVentana: self.$cualVentana)
                //break
            case 2:
                VistaAgregarArticulo(cualVentana: self.$cualVentana)
                //break
            default:
                ContentView(cualVentana: self.$cualVentana)
            }
        }
    }
}
