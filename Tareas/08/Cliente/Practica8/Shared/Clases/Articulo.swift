//
//  Articulo.swift
//  Practica8
//
//  Created by René Payán Téllez on 15/11/21.
//

import Foundation
struct Articulo: Codable{
    var idArticulo: Int32
    var descripcion: String
    var precio: Double
    var inventario: Int64
    var foto: Foto
    public func toJSON()->[String:Any]{
        let respuesta:[String:Any] = [
            "idArticulo":self.idArticulo,
            "descripcion": self.descripcion,
            "precio": self.precio,
            "inventario": self.inventario,
            "foto": self.foto.toJSON()
        ]
        return respuesta
    }
}
