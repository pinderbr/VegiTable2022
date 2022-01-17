//
//  GreenhouseVC.swift
//  VegiTable
//

import UIKit

// implemented to conform to the Downloadable protocol
extension GreenhouseVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            bucketList = data as! [BucketsTemp]
            greenhouseCollection.reloadData()
        }
    }
}
class GreenhouseVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    @IBAction func refreshData() {
        model.getBucketByUser(id: 7, url: ApiCalls.getBucketByUser)
    }
    
    @IBOutlet weak var greenhouseCollection: UICollectionView!
    let cellReuseId = "BucketCell"
    
    // for navigation between ContainerVC child views
    public var containerDelegate: ContainerDelegate?
    
    public var bucketList:[BucketsTemp] = []
    // used for api functions
    let model = BucketsModel()
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        model.delegate = self
        // get buckets for current user
        model.getBucketByUser(id: 7, url: ApiCalls.getBucketByUser)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        greenhouseCollection.dataSource = self
        greenhouseCollection.delegate = self
        
        let layout = UICollectionViewFlowLayout()
        layout.minimumLineSpacing = 20
        layout.minimumInteritemSpacing = 20
        greenhouseCollection.collectionViewLayout = layout
        
        greenhouseCollection.backgroundColor = UIColor.init(named: "VT_BgColor")!
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return bucketList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = greenhouseCollection.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! GreenhouseCollectionViewCell
        cell.layer.borderWidth = 2
        cell.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
        
        let index = indexPath.row
        if(bucketList.count > 0) {
            cell.bucketNameLbl.text = bucketList[index].bucketName
            cell.deviceIdLbl.text = "Device Id: " + String(bucketList[index].deviceId_fk)
        }
        
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 336, height: 120)
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let currentBucket = bucketList[indexPath.row]
        containerDelegate?.displayNewView(storyboardID: "ViewBucketVC", viewTitle: "View Bucket", data: currentBucket.bucketId)
    }
    
    @IBAction func onNewBucketClick() {
        containerDelegate?.displayNewView(storyboardID: "AddBucketVC", viewTitle: "Add Bucket", data: nil)
    }
    
    @IBAction func onEditBucketClick() {
        containerDelegate?.displayNewView(storyboardID: "EditBucketVC", viewTitle: "Edit Bucket", data: nil)
    }

}
