//
//  PlantVC.swift
//  VegiTable
//
//  Created by Bryce Pinder on 2021-04-03.
//

import UIKit

class BucketVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    @IBOutlet weak var plantCollection: UICollectionView!
    let cellReuseId = "PlantCell"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        plantCollection.dataSource = self
        plantCollection.delegate = self
        
        let layout = UICollectionViewFlowLayout()
        layout.minimumLineSpacing = 20
        layout.minimumInteritemSpacing = 20
        plantCollection.collectionViewLayout = layout
        
        plantCollection.backgroundColor = UIColor.init(named: "VT_BgColor")!
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 6
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = plantCollection.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! BucketCollectionViewCell
        
        cell.layer.borderWidth = 2
        cell.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 336, height: 170)
    }
    
}
